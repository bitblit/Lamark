package com.erigir.mozart;

public class Utils {
    public static void main(String[] args) {
        ScaleEnum[] vals = ScaleEnum.values();
        for (int i = 0; i < vals.length; i++) {
            out(vals[i].name() + " : " + vals[i].scale());
        }
    }

    public static void out(Object o) {
        if (null != o) {
            System.out.println(o);
        } else {
            System.out.println(o.toString());
        }
    }
}
