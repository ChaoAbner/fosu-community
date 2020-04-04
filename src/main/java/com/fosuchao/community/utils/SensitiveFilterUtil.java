package com.fosuchao.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

/**
 * @description: 敏感词过滤工具，采用trie树
 * @author: Joker Ye
 * @create: 2020/4/3 10:53
 */

@Component
public class SensitiveFilterUtil {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilterUtil.class);

    private static final String REPLACE_WORD = "**";

    private TrieNode root;

    private String path = "sensitive-words.txt";

    /**
     * 初始化trie树
     */
    @PostConstruct
    void init() {
        root = new TrieNode();
        // 读取敏感词库
        try (
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        ) {
            String keyword;
            while ((keyword = br.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (Exception e) {
            logger.error("trie树初始化失败：" + e.getMessage());
        }
    }

    // 将一个敏感词添加到trie树
    private void addKeyword(String keyword) {
        TrieNode tempNode = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                // subNode为空，创建新节点，并且赋值给temp
                subNode = new TrieNode();
                tempNode.setSubNode(c, subNode);

            }
            tempNode = subNode;

            if (i == keyword.length() - 1) {
                // 敏感词结尾
                tempNode.setWordEnd();
            }
        }
    }

    /**
     * 过滤敏感词
     * @Param [text] 待过滤的文本
     * @return java.lang.String
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        StringBuilder sb = new StringBuilder();

        // 敏感词树上的指针
        TrieNode tempNode = root;
        // text中敏感词的起点
        int begin = 0;
        // text中敏感词的终点，如果trieNode的end为true的话，将begin到position的字符替换为**
        int position = 0;

        while (position < text.length()) {
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)) {
                // 若指针1处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == root) {
                    sb.append(c);
                    begin++;
                }
                // 无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++begin;
                // 重新指向根节点
                tempNode = root;
            } else if (tempNode.isWordEnd()) {
                // 发现敏感词,将begin~position字符串替换掉
                sb.append(REPLACE_WORD);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = root;
            } else {
                // 检查下一个字符
                position++;
            }
        }

        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        // 0x2E80~0x9FFF 是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }


    class TrieNode {
        // 子节点(key是下级字符,value是下级节点)
        HashMap<Character, TrieNode> nodes = new HashMap<>();
        // 关键词结束标识
        boolean isEnd = false;

        public boolean isWordEnd() {
            return isEnd;
        }

        public void setWordEnd() {
            this.isEnd = true;
        }

        public TrieNode getSubNode(Character c) {
            return nodes.get(c);
        }

        public void setSubNode(Character c, TrieNode node) {
            nodes.put(c, node);
        }
    }

}
