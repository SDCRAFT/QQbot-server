package me.heartalborada.utils;

import static java.lang.String.format;

public class size {
    /**
     * 生成size
     *
     * @param size byte
     * @return formatData
     */
    public static String getSize(long size){
        String[] map = {"B","KB","MB","GB"};
        StringBuilder s = new StringBuilder();
        for(int i=0;i<4;i++){
            long c = (long) Math.pow(1024,i);
            if(((float)size)/c < 1){
                s.append(format("%.2f%s",((float)size)/(long) Math.pow(1024,i-1),map[i-1]));
                break;
            }
        }
        return s.toString();
    }
}
