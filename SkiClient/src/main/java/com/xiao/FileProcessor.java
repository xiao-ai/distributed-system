package com.xiao;

import bsdsass2testdata.RFIDLiftData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ian Gorton
 */

public class FileProcessor {
    public List<RFIDLiftData> readFile() {
        // file and stream for input
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        List<RFIDLiftData> RFIDDataIn = new ArrayList<>();

        try {
            // day 1
//            fis = new FileInputStream("/Users/xiao/IdeaProjects/SkiClient/src/main/java/bsdsass2testdata/BSDSAssignment2Day1.ser");

            // day 2
            fis = new FileInputStream("/Users/xiao/IdeaProjects/SkiClient/src/main/java/bsdsass2testdata/BSDSAssignment2Day2.ser");
            ois = new ObjectInputStream(fis);

            RFIDDataIn = (ArrayList) ois.readObject();

            ois.close();
            fis.close();

            System.out.println("The number of lines of the file is " + RFIDDataIn.size());
            return RFIDDataIn;

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }

        return RFIDDataIn;
    }

    public List<List<RFIDLiftData>> partition(final int L) {
        final List<RFIDLiftData> list = readFile();
        final List<List<RFIDLiftData>> partitionedList = new ArrayList<>();
        final int sublistSize = list.size() / L;
        int remainder = list.size() % L;
        int iTake = sublistSize;

        for (int i = 0, iT = list.size(); i < iT; i += iTake) {
            if (remainder > 0) {
                remainder--;
                iTake = sublistSize + 1;
            } else {
                iTake = sublistSize;
            }

            partitionedList.add(new ArrayList<>(list.subList(i, Math.min(iT, i + iTake))));
        }

        return partitionedList;
    }
}
