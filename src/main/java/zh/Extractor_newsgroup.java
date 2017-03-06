package zh;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zwj on 2017/2/23.
 */
public class Extractor_newsgroup {
    public static void main(String[] args) throws IOException {
        extractor();
    }

    public static void extractor() throws IOException {
        String path = "G:\\张鹤\\newsgroup_cut\\20newsgroup.data";
        String labelPath = "G:\\张鹤\\newsgroup_cut\\20newsgroup.tag";
        List<String> alls = FileUtils.readLines(new File(path));
        List<String> labels = FileUtils.readLines(new File(labelPath));
        Map<String, List<String>> maps = new HashMap<>();
        List<String> selectData = new ArrayList<>();
        List<String> selectLabel = new ArrayList<>();
        for (int i = 0; i < alls.size(); i++) {
            String label = labels.get(i);
            if (label.equals("1")
                    || label.equals("7")
                    || label.equals("11")) {
                selectData.add(alls.get(i).trim());
                selectLabel.add(label);
            }
        }
        FileUtils.writeLines(new File("G:\\张鹤\\newsgroup_cut\\cross6\\newsgroup_selectData.txt"), selectData);
        FileUtils.writeLines(new File("G:\\张鹤\\newsgroup_cut\\cross6\\newsgroup_selectLabel.txt"), selectLabel);
//        Set<Map.Entry<String, List<String>>> entries = maps.entrySet();
//        for (Map.Entry<String, List<String>> entry : entries) {
//            FileUtils.writeLines(new File("G:\\张鹤\\newsgroup_original\\" + entry.getKey()), entry.getValue());
//        }
    }
}
