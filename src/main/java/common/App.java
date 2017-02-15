package common;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {


    public static void main(String[] args) throws IOException, InterruptedException {
        File file = new File("C:\\diff");
        file.mkdir();
        diff();
    }

    public static void diff() throws IOException, InterruptedException {
        List<String> strings = Files.readAllLines(Paths.get("C:\\t_sort_GNMF-longtail.txt"), Charset.defaultCharset());
        Set<String> GNMF_longtail = strings.stream()
                .filter(line -> !line.startsWith("Topic "))
                .map(line -> line.split(":")[0])
                .collect(Collectors.toSet());

        strings = Files.readAllLines(Paths.get("C:\\t_sort_GNMF.txt"), Charset.defaultCharset());
        Set<String> sort_GNMF = strings.stream()
                .filter(line -> !line.startsWith("Topic "))
                .map(line -> line.split(":")[0])
                .collect(Collectors.toSet());

        strings = Files.readAllLines(Paths.get("C:\\t_sort_NMF.txt"), Charset.defaultCharset());
        Set<String> sort_NMF = strings.stream()
                .filter(line -> !line.startsWith("Topic "))
                .map(line -> line.split(":")[0])
                .collect(Collectors.toSet());

        List<String> collect = GNMF_longtail.stream()
                .filter(line -> !sort_GNMF.contains(line))
                .collect(Collectors.toList());
        Files.write(Paths.get("C:\\diff\\GNMF_longtail_diff_GNMF.txt"), collect, Charset.defaultCharset());

        collect = sort_GNMF.stream()
                .filter(line -> !GNMF_longtail.contains(line))
                .collect(Collectors.toList());
        Files.write(Paths.get("C:\\diff\\GNMF_diff_GNMF_longtail.txt"), collect, Charset.defaultCharset());

        collect = GNMF_longtail.stream()
                .filter(line -> !sort_NMF.contains(line))
                .collect(Collectors.toList());
        Files.write(Paths.get("C:\\diff\\GNMF_longtail_diff_NMF.txt"), collect, Charset.defaultCharset());


        collect = sort_NMF.stream()
                .filter(line -> !GNMF_longtail.contains(line))
                .collect(Collectors.toList());
        Files.write(Paths.get("C:\\diff\\NMF_diff_GNMF_longtail.txt"), collect, Charset.defaultCharset());
    }


}

