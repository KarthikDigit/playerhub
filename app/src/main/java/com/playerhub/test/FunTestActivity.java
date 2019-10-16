package com.playerhub.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.playerhub.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FunTestActivity extends AppCompatActivity {

    private static final String TAG = "FunTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun_test);

        List<Integer> a = new ArrayList<>(); //{6, 1, 1, 2, 2, 3, 3, 2};

        a.add(6);
        a.add(1);
        a.add(1);
        a.add(2);
        a.add(2);
        a.add(3);
        a.add(3);
        a.add(2);

        int k = 1;


        Log.e(TAG, "onCreate: Map pair " + makePairMap(a).toString());

        List<IntModel> pair = makePair(a);
        Log.e(TAG, "onCreate: " + pair.toString());

        Log.e(TAG, "onCreate: ----------------------------------");
        int count = count(pair, k);

        Log.e(TAG, "onCreate: Count is " + count);


        Log.e(TAG, "onCreate: " + addition(4.1f, 6.3f));
        Log.e(TAG, "onCreate: " + addition(4, 6.3f));
        Log.e(TAG, "onCreate: " + addition(4.5f, 6));
        Log.e(TAG, "onCreate: " + addition(4, 6));

    }

//    // Function to remove duplicates from an ArrayList
//    public static List<Integer[]> removeDuplicates(List<Integer[]> list) {
//
//        // Create a new ArrayList
//        ArrayList<Integer[]> newList = new ArrayList<Integer[]>();
//
//        // Traverse through the first list
//        for (Integer[] element : list) {
//
//
//            for (Integer[] e : list
//                    ) {
//
//                int a = element[0];
//                int b = element[1];
//
//                int a1 = e[0];
//                int b1 = e[1];
//
//                if (a != a1 && b != b1) {
//                    newList.add(element);
//                }
//
//            }
//
//            // If this element is not present in newList
//            // then add it
////            if (!newList.contains(element)) {
////
////                newList.add(element);
////            }
//        }
//
//        // return the new list
//        return newList;
//    }

    private int count(List<IntModel> list, int k) {

        Set<IntModel> removeDuplicate = new LinkedHashSet<>(list);
//        removeDuplicate.addAll(list);

        Log.e(TAG, "count: " + list.size());

//        List<Integer[]> removeDuplicate = removeDuplicates(list);

        Log.e(TAG, "count: " + removeDuplicate.size());

        list.clear();
        list.addAll(removeDuplicate);

        int count = 0;
        for (int i = 0; i < list.size(); i++) {


            IntModel list1 = list.get(i);

            int a = list1.a;
            int b = list1.b;

            int c = a + k;
            Log.e(TAG, "count: pair " + a + "  " + b + "  " + c);
            if (c == b) count++;

        }


        return count;

    }


    private Set<IntModel> makePairMap(List<Integer> a) {

        Set<IntModel> mapPair = new HashSet<>();

        for (int i = 0; i < a.size(); i++) {


            for (int j = i + 1; j < a.size() - 1; j++) {

                if (a.get(i) <= a.get(j)) {

                    IntModel intModel = new IntModel();
                    intModel.a = a.get(i);
                    intModel.b = a.get(j);

                    mapPair.add(intModel);

                }

            }


        }

        return mapPair;
    }

    private List<IntModel> makePair(List<Integer> a) {

        List<IntModel> pair = new ArrayList<>();

        for (int i = 0; i < a.size(); i++) {


            for (int j = i + 1; j < a.size() - 1; j++) {

                if (a.get(i) <= a.get(j)) {

                    IntModel intModel = new IntModel();
                    intModel.a = a.get(i);
                    intModel.b = a.get(j);

                    pair.add(intModel);
                }

            }


        }

        return pair;

    }




    class IntModel {

        int a, b;

        @Override
        public String toString() {
            return "{" + a +
                    "," + b +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IntModel)) return false;

            IntModel intModel = (IntModel) o;

            if (a != intModel.a) return false;
            return b == intModel.b;
        }

        @Override
        public int hashCode() {
            int result = a;
            result = 31 * result + b;
            return result;
        }
    }


    public int addition(float numa, float numb) {
        // will return float
        return (int) Math.round(numa + numb);
    }

    public int addition(int numa, float numb) {
        // explicitly cast to int
        return (int) Math.round(numa + (int) numb);
    }

    public int addition(float numa, int numb) {
        // explicitly cast to int
        return (int) Math.round(numa + numb);
    }

    public int addition(int numa, int numb) {
        // will return int
        return Math.round(numa + numb);
    }

}
