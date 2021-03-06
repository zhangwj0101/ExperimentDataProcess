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
import java.util.List;

/**
 * @author zwj
 */
public class ToJSON2D {

    public static void main(String[] args) throws IOException {
        String cat = "sci_talk";
//        String BasePath = "C:\\20170125毕业设计\\tsne_show\\echarts\\data\\comp_rec_tsne_our.csv";
        String base = "C:\\20170125毕业设计\\20newsgroup数据集及结果\\20NG_matlabformat\\%s\\%s_tsne_%s.csv";
        String[] methods = {"mtrick", "DTL", "TriTL", "our"};
        for (String cString : methods) {
            go2json(String.format(base, cat, cat, cString));
        }
    }

    public static void go2json(String path) throws IOException {
        List<String> readLines = FileUtils.readLines(new File(path));
        List<String[]> cat1 = new ArrayList<>();
        List<String[]> cat2 = new ArrayList<>();
        for (String line : readLines) {
            String[] split = line.split(",");
            if (split[2].equals("1")) {
                cat1.add(split);
            } else if (split[2].equals("2")) {
                cat2.add(split);
            } else {
                break;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[[");
        for (String[] li : cat1) {
            sb.append("[").append(li[0]).append(",").append(li[1]).append("],");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("],[");
        for (String[] li : cat2) {
            sb.append("[").append(li[0]).append(",").append(li[1]).append("],");
        }
        sb.deleteCharAt(sb.length() - 1);
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
