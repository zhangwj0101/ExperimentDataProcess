package lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;


public class LatentDirichletAllocation {

    public int K = 100;

    public double alpha = 0.1;
    public double beta = 0.01;

    public int niters = 100;

    public int[][] zAssigns;

    public int ndk[][];
    public int ndkSum[];
    public int nkw[][];
    public int nkwSum[];

    public int M;
    public int V;
    public List<List<Integer>> docs = new ArrayList<List<Integer>>();
    public HashMap<String, Integer> w2i = new HashMap<String, Integer>();
    public HashMap<Integer, String> i2w = new HashMap<Integer, String>();

    public List<Map<Integer, Integer>> docsInMap = new ArrayList<Map<Integer, Integer>>();

    public void loadTxtsIntoMaps(String txtPath) {
        BufferedReader reader = IOUtil.getReader(txtPath, "UTF-8");

        String line;
        try {
            line = reader.readLine();
            while (line != null) {
                Map<Integer, Integer> doc = new HashMap<Integer, Integer>();

                String[] tokens = line.trim().split("\\s+");
                for (String token : tokens) {
                    if (!w2i.containsKey(token)) {
                        w2i.put(token, w2i.size());
                        i2w.put(w2i.get(token), token);
                    }
                    if (doc.containsKey(token))
                        doc.put(w2i.get(token), doc.get(token) + 1);
                    else
                        doc.put(w2i.get(token), 1);
                }
                docsInMap.add(doc);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        M = docs.size();
        V = w2i.size();
        System.out.println(M);
        System.out.println(V);
        return;
    }

    public void loadTxts(String txtPath) {
        BufferedReader reader = IOUtil.getReader(txtPath, "UTF-8");

        String line;
        try {
            line = reader.readLine();
            while (line != null) {
                List<Integer> doc = new ArrayList<Integer>();

                String[] tokens = line.trim().split("\\s+");
                for (String token : tokens) {
                    if (!w2i.containsKey(token)) {
                        w2i.put(token, w2i.size());
                        i2w.put(w2i.get(token), token);
                    }
                    doc.add(w2i.get(token));
                }
                docs.add(doc);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        M = docs.size();
        V = w2i.size();
        System.out.println(M);
        System.out.println(V);
        return;
    }

    public void loadTxts(String txtPath, String dicPath) {
        BufferedReader reader = IOUtil.getReader(txtPath, "UTF-8");
        BufferedReader reader2 = IOUtil.getReader(dicPath, "UTF-8");

        String line;
        try {
            line = reader2.readLine();
            while (line != null) {
                w2i.put(line, w2i.size());
                i2w.put(w2i.size() - 1, line);
                line = reader2.readLine();
            }
            reader2.close();

            line = reader.readLine();
            while (line != null) {
                List<Integer> doc = new ArrayList<Integer>();

                String[] tokens = line.trim().split("\\s+");
                for (String token : tokens) {
                    if (w2i.containsKey(token)) {
                        doc.add(w2i.get(token));
                    }
                }
                docs.add(doc);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        M = docs.size();
        V = w2i.size();

        return;
    }

    public void initLDA() {
        ndk = new int[M][K];
        ndkSum = new int[M];
        nkw = new int[K][V];
        nkwSum = new int[K];

        zAssigns = new int[M][];

        for (int m = 0; m != M; m++) {
            int N = docs.get(m).size();

            zAssigns[m] = new int[N];

            for (int n = 0; n != N; n++) {
                int w = docs.get(m).get(n);
                int z = (int) Math.floor(Math.random() * K);

                nkw[z][w]++;
                nkwSum[z]++;
                ndk[m][z]++;
                ndkSum[m]++;

                zAssigns[m][n] = z;
            }
        }

        return;
    }

    public void sampleZ(int m, int n) {
        int z = zAssigns[m][n];
        int w = docs.get(m).get(n);

        nkw[z][w]--;
        nkwSum[z]--;
        ndk[m][z]--;
        ndkSum[m]--;

        double VBeta = V * beta;
        double KAlpha = K * alpha;

        double[] pTable = new double[K];

        for (int k = 0; k != K; k++) {
            pTable[k] = (ndk[m][k] + alpha) / (ndkSum[m] + KAlpha)
                    * (nkw[k][w] + beta) / (nkwSum[k] + VBeta);
        }

        for (int k = 1; k != K; k++) {
            pTable[k] += pTable[k - 1];
        }

        double r = Math.random() * pTable[K - 1];

        for (int k = 0; k != K; k++) {
            if (pTable[k] > r) {
                z = k;
                break;
            }
        }

        nkw[z][w]++;
        nkwSum[z]++;
        ndk[m][z]++;
        ndkSum[m]++;

        zAssigns[m][n] = z;

        return;
    }

    public void estimate() {
        for (int iter = 0; iter != niters; iter++) {
            System.out.println("LDA Iteration: " + iter + " ...");
            long begin = System.currentTimeMillis();
            for (int m = 0; m != M; m++) {
                int N = docs.get(m).size();
                for (int n = 0; n != N; n++) {
                    sampleZ(m, n);
                }
            }
            System.out.println((System.currentTimeMillis() - begin) / 1000.0);
        }
        return;
    }

    public double[][] computePhi() {
        double[][] phi = new double[K][V];
        for (int k = 0; k != K; k++) {
            for (int v = 0; v != V; v++) {
                phi[k][v] = (nkw[k][v] + beta) / (nkwSum[k] + V * beta);
            }
        }
        return phi;
    }

    public double[][] computeTheta() {
        double[][] theta = new double[M][K];
        for (int m = 0; m != M; m++) {
            for (int k = 0; k != K; k++) {
                theta[m][k] = (ndk[m][k] + alpha) / (ndkSum[m] + K * alpha);
            }
        }
        return theta;
    }

    public ArrayList<List<Entry<String, Double>>> sortedTopicWords(
            double[][] phi, int T) {
        ArrayList<List<Entry<String, Double>>> res = new ArrayList<List<Entry<String, Double>>>();
        for (int k = 0; k != T; k++) {
            HashMap<String, Double> term2weight = new HashMap<String, Double>();
            for (String term : w2i.keySet())
                term2weight.put(term, phi[k][w2i.get(term)]);

            List<Entry<String, Double>> pairs = new ArrayList<Entry<String, Double>>(
                    term2weight.entrySet());
            Collections.sort(pairs, new Comparator<Entry<String, Double>>() {
                public int compare(Entry<String, Double> o1,
                                   Entry<String, Double> o2) {
                    return (o2.getValue().compareTo(o1.getValue()));
                }
            });

            res.add(pairs);
        }

        return res;
    }

    public void printTopics(String path, int top_n) {
        BufferedWriter writer = IOUtil.getWriter(path, "utf-8");
        double[][] phi = computePhi();
        ArrayList<List<Entry<String, Double>>> pairsList = this
                .sortedTopicWords(phi, K);
        try {
            for (int k = 0; k != K; k++) {
                System.out.println("Topic " + k + ":");
                writer.append("Topic " + k + ":\r\n");
                for (int i = 0; i != top_n; i++) {
                    System.out.print(pairsList.get(k).get(i).getKey() + " "
                            + pairsList.get(k).get(i).getValue());
                    writer.append(pairsList.get(k).get(i).getKey() + ":"
                            + pairsList.get(k).get(i).getValue() + "\r\n");
                }
                System.out.println();
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePhi(String path) {
        BufferedWriter writer = IOUtil.getWriter(path, "utf-8");

        double[][] phi = computePhi();
        int K = phi.length;
        assert K > 0;
        int V = phi[0].length;

        try {
            for (int v = 0; v != V; v++) {
                writer.append(phi[0][v] + "");
                for (int k = 0; k != K; k++)
                    writer.append("," + phi[k][v]);
                writer.append("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    public void saveTheta(String path) {
        BufferedWriter writer = IOUtil.getWriter(path, "utf-8");

        double[][] theta = computeTheta();
        int K = theta.length;
        assert K > 0;
        int V = theta[0].length;

        try {
            for (int v = 0; v != V; v++) {
                writer.append(theta[0][v] + "");
                for (int k = 1; k != K; k++)
                    writer.append("," + theta[k][v]);
                writer.append("\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    public void saveWordmap(String path) {
        BufferedWriter writer = IOUtil.getWriter(path, "utf-8");

        try {
            for (String word : w2i.keySet())
                writer.append(word + "\r\n");
//				writer.append(word + "\t" + w2i.get(word) + "\n");

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    public static void main(String args[]) {
        LatentDirichletAllocation lda = new LatentDirichletAllocation();

//		String inPath = "E:\\zhanghe\\研究僧\\毕业设计\\10. final experiment\\sougou\\";
//		String outPath = "E:\\zhanghe\\研究僧\\毕业设计\\10. final experiment\\sougou\\LDA\\";
//		lda.loadTxts(inPath + "sougou2500_dis.txt", inPath + "sougou2500_dis.dic");

        String inPath = "G:\\NMF_KL测试\\13\\";
        String outPath = "G:\\NMF_KL测试\\13\\LDA\\";
        lda.loadTxts(inPath + "13_tf_Format.txt", inPath + "selectWordDic.txt");

        lda.initLDA();
        lda.estimate();
        lda.printTopics(outPath + "lda_topics.txt", 20);
        lda.saveTheta(outPath + "lda_theta.csv");
        lda.savePhi(outPath + "lda_phi.csv");
//		lda.saveWordmap(outPath + "/20newsgroup281217Sort.dic");
    }

}
