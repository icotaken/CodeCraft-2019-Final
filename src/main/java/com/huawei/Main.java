package com.huawei;

//import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = (Logger) Logger.getLogger(String.valueOf(Main.class));
    public static void main(String[] args) throws IOException {
        /*****************以下注释内容在提交时，需取消注释*****************/
//        if (args.length != 4) {
//            logger.error("please input args: inputFilePath, resultFilePath");
//            return;
//        }

        logger.info("Start...");
        /*****************以下注释内容在提交时，需取消注释*****************/
//        String carPath = args[0];
//        String roadPath = args[1];
//        String crossPath = args[2];
//        String answerPath = args[3];
        String carPath = "C:\\Users\\Administrator\\Desktop\\SDK_java\\bin\\config\\car.txt";//读取文件
        String roadPath = "C:\\Users\\Administrator\\Desktop\\SDK_java\\bin\\config\\road.txt";//读取文件
        String crossPath = "C:\\Users\\Administrator\\Desktop\\SDK_java\\bin\\config\\cross.txt";//读取文件
        String answerPath = "C:\\Users\\Administrator\\Desktop\\SDK_java\\bin\\config\\answer.txt";//写入文件
        /*****************以下注释内容在提交时，需取消注释*****************/
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);
        // TODO:read input files
        logger.info("start read input files");
        int roadNum = 0, carNum = 0, crossNum = 0;
        List roadList = readFile(roadPath), carList = readFile(carPath), crossList = readFile(crossPath);
        /*****************将三个txt文件中的数据转换为List格式的数据结构，
         并命名为roadFinalformat, carDispatch, crossFinalformat*****************/
        List<Map<String, Integer>> roadFinalformat = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();//存储每条道路的所有信息
        Set<Integer> roadSpeed = new HashSet<>();//存储道路所有可能的限速
        Set<Integer> channel = new LinkedHashSet<>();//存储道路所有可能的channel
        for (int i = 0; i < roadList.size(); map = new HashMap<>(), i = i +7){
            int roadId = (Integer) roadList.get(i);
            map.put("id", roadId);
            map.put("length", (Integer)roadList.get(i+1));
            map.put("speed", (Integer) roadList.get(i+2));
            roadSpeed.add((Integer) roadList.get(i+2));
            map.put("channel", (Integer) roadList.get(i+3));
            channel.add((Integer) roadList.get(i+3));
            map.put("from", (Integer) roadList.get(i+4));
            map.put("to", (Integer) roadList.get(i+5));
            map.put("isDuplex", (Integer) roadList.get(i+6));
            roadFinalformat.add(map);
            roadNum++;
        }
//        List<Set> crossFinalformat = new ArrayList<>();
//        List<Map<Integer, Set>> crossFinalformat = new LinkedList<>();
        Map<Integer, Set> crossFinalformat = new HashMap<>();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < crossList.size(); set = new HashSet<>(), i = i + 5){
            for (int j = i + 1; j - i < 5; j++)
                set.add((Integer) crossList.get(j));
            crossFinalformat.put((Integer) crossList.get(i), set);
            crossNum++;
        }
        List<Map<String, Integer>> carDispatch = new ArrayList<>();
        Set<Integer> carSpeed = new HashSet<>();//存储所有可能的车速
        Set<Integer> planTime = new LinkedHashSet<>();//存储所有可能的车速
        for (int i = 0; i < carList.size();map = new HashMap<>(), i = i+5){
            map.put("id", (Integer) carList.get(i));
            map.put("from", (Integer) carList.get(i + 1));
            map.put("to", (Integer) carList.get(i + 2));
            map.put("speed", (Integer) carList.get(i + 3));
            carSpeed.add((Integer) carList.get(i + 3));
            map.put("planTime", (Integer) carList.get(i + 4));
            planTime.add((Integer)carList.get(i + 4));
            carDispatch.add(map);
            carNum++;
        }
        // TODO: calc
        /*****************将cross.txt中的数据抽象成图（邻接矩阵）的形式*****************/
        double[][] dist = new double[crossNum][crossNum];//dist -- 长度数组。即，dist[i][j]=sum表示，"顶点i"到"顶点j"的最短路径的长度是sum
        int[][] path = new int[crossNum][crossNum];//path -- 路径。path[i][j]=k表示，"顶点i"到"顶点j"的最短路径会经过顶点k
        for (int i = 0; i < crossNum; i++) {
            double INF = Double.POSITIVE_INFINITY;
            for (int j = 0; j < crossNum; j++) {
                dist[i][j] = INF;//长度数组初始大小为无穷大
                path[i][j] = j;
            }
        }
        int maxChannel = 0;//寻找道路的最大channel
        for (Integer item:channel)
            if (item > maxChannel)
                maxChannel = item;
        for (int i = 0; i < roadNum; i++){//每条道路必定连接两个路口
            int temp = 0, from = 0, to = 0;
            int startCross = roadFinalformat.get(i).get("from");
            int endCross = roadFinalformat.get(i).get("to");
            for (Map.Entry<Integer, Set> entry : crossFinalformat.entrySet()){
                if (entry.getKey() == startCross)
                    from = temp;
                else if (entry.getKey() == endCross)
                    to = temp;
                temp++;
            }
//            double channelCoef = maxChannel / roadFinalformat.get(i).get("channel");//channelCoef -->道路系数
            double channelCoef = 6 - 0.8 * roadFinalformat.get(i).get("channel");//channelCoef -->道路系数
            if (roadFinalformat.get(i).get("isDuplex") == 1){
                dist[from][to]  = channelCoef * roadFinalformat.get(i).get("length");
                dist[to][from] = channelCoef * roadFinalformat.get(i).get("length");
            }
            else
                dist[from][to] = channelCoef * roadFinalformat.get(i).get("length");
        }
        int[][] matrixForHandle = minRouteByFloyd(dist, path);
        /*****************为每辆车规划最短的路径*****************/
        List<List<Integer>> finalAnswer = new LinkedList<>();
        List<Integer> finalRoute = new LinkedList<>();
        int maxplanTime = 0;
        for (Integer item:planTime)
            if (item > maxplanTime)
                maxplanTime = item;
//        int[] carTimeArray = new int[maxplanTime];//每个发车时间对应的车辆
        int[] carSpeedArray = new int[carSpeed.size()];
        int[] startTime = new int[carSpeed.size()];
        int carNumOfEveryTime = 25;//每个时间片的发车数目
//        int sizeOfPlanTime = planTime.size();
        for (int i = 0; i < carNum; i++){
            int carSpeedNow = carDispatch.get(i).get("speed");
            int level = chargeSpeedLevel(carSpeed, carSpeedNow);//判断属于第几档的车速
            carSpeedArray[level]++;
        }
        for (int i = 0; i < startTime.length; i++)
            startTime[i] = carSpeedArray[i] / carNumOfEveryTime + 1;
        for (int i = 0; i < carNum; finalRoute = new LinkedList<>(), i++){
            finalRoute.add(carDispatch.get(i).get("id"));//按任务书要求格式，添加每辆车的输出信息...
            finalRoute.add(i / carNumOfEveryTime + maxplanTime);
//            int carSpeedNow = carDispatch.get(i).get("speed");
//            int planTimeDefault = carDispatch.get(i).get("planTime");
//            int level = chargeSpeedLevel(carSpeed, carSpeedNow);//判断属于第几档的车速
//            int lastLevel = 0;
//            carSpeedArray[level]--;
//            for (int k = carSpeed.size()-1; k >= 0; k--) {
//                lastLevel += startTime[k];
//                if (level == k){//先发车速最高档的车辆
//                    int pT = lastLevel - carSpeedArray[k] / carNumOfEveryTime;
//                    if (planTimeDefault < pT)
//                        finalRoute.add(pT);
//                    else
//                        finalRoute.add(planTimeDefault);
//                    break;
//                }
//            }
            int temp = 0, from = 0, to = 0;
            int startCross = carDispatch.get(i).get("from");//起始路口序号（从0开始）
            int endCross = carDispatch.get(i).get("to");//终止路口序号（从0开始）
            Set<Integer> a = new HashSet<>();
            Set<Integer> b = new HashSet<>();
            for (Map.Entry<Integer, Set> entry : crossFinalformat.entrySet()){
                if (entry.getKey() == startCross){
                    from = temp;
                    a = entry.getValue();
                }
                else if (entry.getKey() == endCross){
                    to = temp;
                    b = entry.getValue();
                }
                if (a.size() != 0 && b.size() != 0)
                    break;
                else
                    temp++;
            }
//            int carTime = carDispatch.get(i).get("planTime");//数组从0开始
//            if (i < 100)
//                finalRoute.add(carTime);
//            else if ((i/23 + 10)>=200 && (i/23 + 10) % 200 < 5)
//                finalRoute.add(i/23 + 10 + 6);
//            else
//                finalRoute.add(i/23 + 10);//发车时间
//            int carTime = carDispatch.get(i).get("planTime") - 1;//数组从0开始
//            carTimeArray[carTime]++;
//            finalRoute.add(carTime + sizeOfPlanTime*(carTimeArray[carTime]/carNumOfEveryTime) + 1);
            for (int cross = matrixForHandle[from][to]; cross != to; cross = matrixForHandle[cross][to]){
                    int index = 0;
                for (Map.Entry<Integer, Set> entry : crossFinalformat.entrySet()){
                    if (index == from)
                        a = entry.getValue();
                    if (index == cross)
                        b = entry.getValue();
                    index++;
                }
                finalRoute.add(intersect(a, b));
                from = cross;
            }
            int index = 0, indexOfnow = 0, indexOfto = 0;
            for (Map.Entry<Integer, Set> entry : crossFinalformat.entrySet()){
                if (index == from)
                    a = entry.getValue();
                if (index == to)
                    b = entry.getValue();
                index++;
            }
            finalRoute.add(intersect(a, b));
            finalAnswer.add(finalRoute);//将每辆车的输出信息添加到大集合中,以进行下一步写入
        }
        /*****************对最短路径位于主干道路上的车辆重新规划路径*****************/
        int maxroadThreshold = 9000, minroadThreshold = 400;//道路出现频率大于（小于）该阈值，则判断为主干（稀疏）道路
//        double dividedBySparsedroad = 1/10, multiplyMainroad = 10;//将稀疏道路的权重下降，将主干道路的权重上升
        for(int cycleTimes = 1; cycleTimes > 0; cycleTimes--) {//分流次数
            //1.遍历所有finalAnswer,确定每条道路出现的次数，更新occurOfRoadId
            Map<Integer, Integer> newOccurOfRoadId = getOccurOfRoadId(finalAnswer);
            List<Integer> isSparsedroad = new ArrayList<>();//存储主干道路
            List<Integer> isMainroad = new ArrayList<>();//存储主干道路
            for (Map.Entry<Integer, Integer> entry : newOccurOfRoadId.entrySet())
                if (entry.getValue() > maxroadThreshold)
                    isMainroad.add(entry.getKey());
                else if (entry.getValue() < minroadThreshold)
                    isSparsedroad.add(entry.getKey());
//            System.out.println(isMainroad.size());
            //2.更新isMainRoad,建立新的邻接矩阵
                for (int j = 0; j < roadFinalformat.size(); j++) {//查询该道路连接哪两个路口
                    int road = roadFinalformat.get(j).get("id");
                    int temp = 0, from = 0, to = 0;
                    int startCross = roadFinalformat.get(j).get("from");
                    int endCross = roadFinalformat.get(j).get("to");
                    for (Map.Entry<Integer, Set> entry : crossFinalformat.entrySet()){
                        if (entry.getKey() == startCross)
                            from = temp;
                        else if (entry.getKey() == endCross)
                            to = temp;
                        temp++;
                    }
                    if (isMainroad.contains(road)) {
                        dist[from][to] = Double.POSITIVE_INFINITY;
                        if (roadFinalformat.get(j).get("isDuplex") == 1)
                            dist[to][from] = Double.POSITIVE_INFINITY;
//                        dist[from][to] *= multiplyMainroad;
//                        if (roadFinalformat.get(j).get("isDuplex") == 1)
//                            dist[to][from] *= multiplyMainroad;
                    }
//                    if (isSparsedroad.contains(road)) {
//                        dist[from][to] *= dividedBySparsedroad;
//                        if (roadFinalformat.get(j).get("isDuplex") == 1)
//                            dist[to][from] *= dividedBySparsedroad;
//                    }
                }
            //3.遍历finalAnswer,按一定概率为经过主干道路的车辆规划新的路径
            int[][] newMatrixForHandle = minRouteByFloyd(dist, path);//为车辆规划避开主干道路的新路径
            for (int i = 0; i < finalAnswer.size(); i++) {//检测每辆车是否通过主干道路
                int n = numOfIntersectionInList(finalAnswer.get(i), isMainroad);
                if (n == 0) continue;
                else if (n >= 8) n = 8;
                if (Math.random() < 0.1 + n * 0.05) {
                    int length = finalAnswer.get(i).size();
                    int temp = 0, from = 0, to = 0;
                    int startCross = carDispatch.get(i).get("from");//起始路口序号（从0开始）
                    int endCross = carDispatch.get(i).get("to");//终止路口序号（从0开始）
                    Set<Integer> a = new HashSet<>();
                    Set<Integer> b = new HashSet<>();
                    for (Map.Entry<Integer, Set> entry : crossFinalformat.entrySet()){
                        if (entry.getKey() == startCross){
                            from = temp;
                            a = entry.getValue();
                        }
                        else if (entry.getKey() == endCross){
                            to = temp;
                            b = entry.getValue();
                        }
                        if (a.size() != 0 && b.size() != 0)
                            break;
                        else
                            temp++;
                    }
                    List<Integer> newfinalRoute = new LinkedList<>();
                    List<Integer> oldfinalRoute = new LinkedList<>();
                    oldfinalRoute.addAll(finalAnswer.get(i));
                    for (int cross = newMatrixForHandle[from][to]; cross != to; cross = newMatrixForHandle[cross][to]) {
                        int index = 0;
                        for (Map.Entry<Integer, Set> entryNew : crossFinalformat.entrySet()){
                            if (index == from)
                                a = entryNew.getValue();
                            else if (index == cross)
                                b = entryNew.getValue();
                        }
                        newfinalRoute.add(intersect(a, b));
                        from = cross;
                    }
                    int index = 0;
                    for (Map.Entry<Integer, Set> entry : crossFinalformat.entrySet()){
                        if (index == from)
                            a = entry.getValue();
                        if (index == to)
                            b = entry.getValue();
                        index++;
                    }
                    newfinalRoute.add(intersect(a, b));
                    if (newfinalRoute.size() != 0) {
                        for (int j = length - 1; j != 1; j--)
                            finalAnswer.get(i).remove(j);
                        if (newfinalRoute.contains(0)) {
                            finalAnswer.get(i).clear();
                            finalAnswer.get(i).addAll(oldfinalRoute);
                        } else
                            finalAnswer.get(i).addAll(newfinalRoute);
                    }
                }
            }
        }
        // TODO: write answer.txt
        logger.info("Start write output file");
        writeFile(finalAnswer, answerPath);
        logger.info("End...");

    }
    /*****************读取文件*****************/
    public static List readFile(String pathName) {
        List<Integer> temp = new ArrayList<>();
        try (FileReader reader = new FileReader(pathName);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                if (!line.contains("#")) {
                    for (int i = 0; i < line.length(); i++)
                        if (line.charAt(i) == '(' || line.charAt(i) == ')' || line.charAt(i) == '\n'|| line.charAt(i) == ' ')
                            line = line.substring(0, i) + line.substring(i + 1);
                    String[] tempStr = line.split(",");
                    for (int j = 0; j < tempStr.length; j++)
                        temp.add(Integer.parseInt(tempStr[j]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }
    /*****************写入文件*****************/
    public static void writeFile(List<List<Integer>> finalAnswer, String pathName){
        try {
            File writeName = new File(pathName); // 相对路径，如果没有则要建立一个新的pathName文件
            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                for (List item:finalAnswer){//carId-->item.get(0), planTime-->item.get(1), roadId-->item.get(i)
                    String line = "(" + item.get(0) + "," + item.get(1);
                    for (int i = 2; i < item.size(); i++)
                        line = line + "," + item.get(i);
                    line = line + ")";
                    out.write(line + "\r\n"); // \r\n即为换行
                    out.flush(); // 把缓存区内容压入文件
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*****************Floyd法求最短路径（由于道路速度的限制，对不同车速要规划不同的路径）****************/
    public static int[][] minRouteByFloyd(double[][] dist, int[][] path){
        int crossNum = dist.length;
        double INF = Double.POSITIVE_INFINITY;
        if (crossNum != path.length) return null;
        double[][] newDist = new double[crossNum][crossNum];
        int[][] newPath = new int[crossNum][crossNum];
        for (int i = 0; i < crossNum; i++){
            newDist[i] = dist[i].clone();
            newPath[i] = path[i].clone();
        }
        // 计算最短路径
        for (int k = 0; k < crossNum; k++) {//k为中介值
            for (int i = 0; i < crossNum; i++) {
                for (int j = 0; j < crossNum; j++) {
                    // 如果经过下标为k顶点路径比原两点间路径更短，则更新dist[i][j]和path[i][j]
                    double tmp = (Double.isInfinite(newDist[i][k]) || Double.isInfinite(newDist[k][j])) ? INF : (newDist[i][k] + newDist[k][j]);
                    if (newDist[i][j] > tmp && !Double.isInfinite(tmp)) {
                        // "i到j最短路径"对应的值设，为更小的一个(即经过k)
                        newDist[i][j] = tmp;
                        // "i到j最短路径"对应的路径，经过k
                        newPath[i][j] = newPath[i][k];
                    }
                }
            }
        }
        return newPath;
    }
    /*****************求连通两个相邻路口的道路序号****************/
    public static int intersect(Set<Integer> a, Set<Integer> b){
        int result = 0;
        Set<Integer> temp = new HashSet<>();
        temp.addAll(a);
        temp.retainAll(b);
        for (Integer item:temp)
            if (item != -1)
                result = item;
        return result;
    }
    /*****************求两个List类型的数据，交集的数量****************/
    public static int numOfIntersectionInList(List l1, List l2){
        List<Integer> temp = new LinkedList<>();
        temp.addAll(l1);
        temp.retainAll(l2);
        return temp.size();
    }
    /*****************遍历finalAnswer,确定每条道路出现的次数****************/
    public static Map<Integer, Integer> getOccurOfRoadId(List<List<Integer>> finalAnswer){
        Map<Integer, Integer> occurOfRoadId = new HashMap<>();
        for (List<Integer> item:finalAnswer)
            for (int i = 2; i < item.size(); i++){
                int key = item.get(i);
                if (occurOfRoadId.containsKey(key)){
                    int value = occurOfRoadId.get(key);
                    occurOfRoadId.put(key, ++value);
                }
                else
                    occurOfRoadId.put(key, 1);//第一次出现则加1
            }
        return occurOfRoadId;
    }
    /*****************判断当前车速属于哪一档次****************/
    public static int chargeSpeedLevel(Set<Integer> carSpeed, int carSpeedNow){
        if (!carSpeed.contains(carSpeedNow)) return 1;
        int result = carSpeed.size()-1;//默认是最高档的
        for (Integer item:carSpeed)
            if (item > carSpeedNow)
                result--;
        return result;
    }
    /*****************遍历Map中的键值****************/
    public static Set getSet(Map<Integer, Set> E){
        Set<Integer> set = new HashSet<>();
        for (Map.Entry<Integer, Set> entry : E.entrySet())
            set = entry.getValue();
        return set;
    }
    
}