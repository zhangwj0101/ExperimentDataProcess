package zh;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by zwj on 2017/2/23.
 */
public class LdaFormat {
    public static void main(String[] args) throws IOException {
        String path = "G:\\NMF_KL测试\\13\\137index.csv";
        String dicPath = "G:\\NMF_KL测试\\AllFeature.txt";

        Map<Integer, String> words = new HashMap<>();
        List<String> dicMaps = FileUtils.readLines(new File(dicPath));
        List<String> selectWrods = FileUtils.readLines(new File(path));
        int index = 1;
        for (String wor : dicMaps) {
            words.put(index, wor);
            index++;
        }
        List<String> trueWordLists = new ArrayList<>();
        for (String id : selectWrods) {
            trueWordLists.add(words.get(Integer.valueOf(id)));
        }

        FileUtils.writeLines(new File("G:\\NMF_KL测试\\13\\selectWordDic.txt"), trueWordLists);
//        tras();
    }


    public static void gettf() throws IOException {
        String path = "G:\\NMF_KL测试\\13\\fea_13tf.csv";
        String dicPath = "G:\\NMF_KL测试\\AllFeature.txt";
        Map<Integer, String> words = new HashMap<>();
        List<String> dicMaps = FileUtils.readLines(new File(dicPath));
        int index = 1;
        for (String wor : dicMaps) {
            words.put(index, wor);
            index++;
        }

        List<String> alls = FileUtils.readLines(new File(path));
        System.out.println(alls.get(0).split(",").length);
        List<String> res = new ArrayList<>();
        for (String line : alls) {
            String[] split = line.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                for (int k = 1; k <= Integer.valueOf(split[i]); k++) {
                    sb.append(words.get(i + 1)).append(" ");
                }
            }
            res.add(sb.toString().trim());
        }
        FileUtils.writeLines(new File("G:\\NMF_KL测试\\13\\13_tf_Format.txt"), res);
    }

    public static void tras() throws IOException {
        String path = "G:\\张鹤\\newsgroup_original\\cross4\\newsgroup_selectData.txt";
        List<String> alls = FileUtils.readLines(new File(path));
        String dicPath = "G:\\张鹤\\newsgroup_cut\\vocabulary.txt";
        Set<String> dicSets = new HashSet<>(FileUtils.readLines(new File(dicPath)));
        Set<String> words = new HashSet<>();
        List<String> ldaRes = new ArrayList<>();
        for (String line : alls) {
            String[] split = line.split(" ");
            words.addAll(Arrays.stream(split).collect(Collectors.toSet()));
        }
        List<String> wordLists = new ArrayList<>(words);
        Collections.sort(wordLists);
        Map<String, Integer> wordsDicMaps = new HashMap<>();
        int index = 1;
        for (String word : wordLists) {
            wordsDicMaps.put(word, index++);
        }
        FileUtils.writeLines(new File("G:\\张鹤\\newsgroup_original\\cross4\\sortdcit.txt"), wordLists);
        for (String line : alls) {
            String[] split = line.split(" ");
            Map<String, Integer> docMap = new HashMap<>();
            for (String word : split) {
                if (!dicSets.contains(word)) {
                    continue;
                }
                Integer integer = docMap.get(word);
                if (integer == null) {
                    integer = 1;
                } else {
                    integer = integer + 1;
                }
                docMap.put(word, integer);
            }
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(docMap.entrySet());
            Collections.sort(entries, Comparator.comparing(Map.Entry::getKey));
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Integer> entry : entries) {
                sb.append(wordsDicMaps.get(entry.getKey()) + ":" + entry.getValue() + " ");
            }
            ldaRes.add(sb.toString().trim());
        }
        FileUtils.writeLines(new File("G:\\张鹤\\newsgroup_original\\cross4\\LDA_Format.txt"), ldaRes);
    }
}
