/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package newsgroup;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zwj
 */
public class ToJSON {

    public static void main(String[] args) throws IOException {
        amazon();
        newsgroup();
    }

    public static void amazon() throws IOException {
        File file = new File("C:\\20170125毕业设计\\amazon数据集及结果\\mydata_add_withtraintest\\");
        List<String> paths = new ArrayList<>();
        path(file, paths, "_tsne_");
        for (String path : paths) {
            go2json2(path, path.contains("-d3"));
        }
//        go2json2("C:\\20170125毕业设计\\amazon数据集及结果\\mydata_add_withtraintest\\en_de_books_books\\en_de_books_books_tsne_LSF-d3.csv", true);
//        go2json2("C:\\20170125毕业设计\\amazon数据集及结果\\mydata_add_withtraintest\\en_de_books_books\\en_de_books_books_tsne_mtrick-d3.csv", true);
    }

    public static void newsgroup() throws IOException {

        File file = new File("C:\\20170125毕业设计\\20newsgroup数据集及结果\\20NG_matlabformat\\");
        List<String> paths = new ArrayList<>();
        path(file, paths, "_tsne_");
        for (String path : paths) {
            go2json2(path, path.contains("-d3"));
        }
    }

    static void path(File file, List<String> paths, String filter) {
        if (file.isDirectory()) {
            for (File temp : file.listFiles()) {
                path(temp, paths, filter);
            }
        } else {
            if (file.getName().contains(filter)) {
                paths.add(file.getAbsolutePath());
            }
        }
    }

    public static void newsgroup2() throws IOException {

        String[] cats = {"comp_rec", "comp_sci", "comp_talk", "rec_sci", "rec_talk", "sci_talk"};
//        String BasePath = "C:\\20170125毕业设计\\tsne_show\\echarts\\data\\comp_rec_tsne_our.csv";
        String base2d = "C:\\20170125毕业设计\\20newsgroup数据集及结果\\20NG_matlabformat\\%s\\%s_tsne_%s.csv";
        String base3d = "C:\\20170125毕业设计\\20newsgroup数据集及结果\\20NG_matlabformat\\%s\\%s_tsne_%s-d3.csv";
        String[] methods = {"mtrick", "DTL", "TriTL", "our"};
        boolean is3d = false;
        for (String cat : cats) {
            for (String cString : methods) {
                go2json2(String.format(is3d ? base3d : base2d, cat, cat, cString), is3d);
            }
        }
    }

    public static void go2json2(String path, boolean is3d) throws IOException {
        List<String> readLines = FileUtils.readLines(new File(path));
        if (readLines.size() == 0) {
            System.err.println("empty");
        }
        List<List<String>> cat1 = new ArrayList<>();
        List<List<String>> cat2 = new ArrayList<>();
        for (String line : readLines) {
            String[] split = line.split(",");
            List<String> strings = Arrays.stream(split).collect(Collectors.toList()).subList(0, split.length - 1);
            if (split[split.length - 1].equals("1")) {
                cat1.add(strings);
            } else if (split[split.length - 1].equals("2")) {
                cat2.add(strings);
            } else {
                break;
            }
        }
        String name = "";
        if (path.contains("mtrick")) {
            name = "mtrick";
        } else if (path.contains("DTL")) {
            name = "DTL";
        } else if (path.contains("TriTL")) {
            name = "TriTL";
        } else if (path.contains("our")) {
            name = "LAM";
            path = path.replaceAll("our", "LAM");
        } else if (path.contains("KLS")) {
            name = "LAM";
            path = path.replaceAll("KLS", "LAM");
        } else if (path.contains("LSF")) {
            name = "LSF";
            path = path.replaceAll("LSF", "LSF");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("var " + name + "=").append("[\n").append(cat1.toString()).append("\n,\n").append(cat2.toString()).append("\n];");
        FileUtils.write(new File(path + ".js"), sb.toString());
    }

    public static void go2json(String path, boolean is3d) throws IOException {
        List<String> readLines = FileUtils.readLines(new File(path));
        if (readLines.size() == 0) {
            System.err.println("empty");
        }
        List<String[]> cat1 = new ArrayList<>();
        List<String[]> cat2 = new ArrayList<>();
        for (String line : readLines) {
            String[] split = line.split(",");
            if (split[split.length - 1].equals("1")) {
                cat1.add(split);
            } else if (split[split.length - 1].equals("2")) {
                cat2.add(split);
            } else {
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("[");
        for (int k = 0; k < cat1.size(); k++) {
            String[] li = cat1.get(k);
            sb.append("[");
            for (int i = 0; i < li[i].length() - 1; i++) {
                sb.append(li[i]);
                if (i < li[i].length() - 2) {
                    sb.append(",");
                }
            }
            sb.append("]");
            if (k < cat1.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");

        sb.append(",[");
        for (int k = 0; k < cat2.size(); k++) {
            String[] li = cat2.get(k);
            sb.append("[");
            for (int i = 0; i < li[i].length() - 1; i++) {
                sb.append(li[i]);
                if (i < li[i].length() - 2) {
                    sb.append(",");
                }
            }
            sb.append("]");
            if (k < cat1.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]];");
        String name = "";
        if (path.contains("mtrick")) {
            name = "mtrick";
        } else if (path.contains("DTL")) {
            name = "DTL";
        } else if (path.contains("TriTL")) {
            name = "TriTL";
        } else if (path.contains("our")) {
            name = "our";
        }
        sb.insert(0, "var " + name + "=");
        FileUtils.write(new File(path + ".js"), sb.toString());
    }
}
