package com.example.earth.fuelfriend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;


public class SearchFragment extends Fragment {

    private SearchableAdapter search;
    ArrayList<String> vehicles_list = new ArrayList<>();
    DBHelper dbHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int csvResources[] = {R.raw.vehicles_pt5, R.raw.vehicles_pt2, R.raw.vehicles_pt3, R.raw.vehicles_pt4, R.raw.vehicles_pt1};

        View v = inflater.inflate(R.layout.activity_filter_transport, container, false);
        dbHelper = new DBHelper(getContext());

        search = new SearchableAdapter(getContext(), vehicles_list);
        SearchView sv = (SearchView) v.findViewById(R.id.search_input);
        ListView lv = (ListView) v.findViewById(R.id.transport_list);
        lv.setAdapter(search);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search.getFilter().filter(s);
                return false;
            }
        });

        for (int csvResource : csvResources) {
            InputStream inputStream = getResources().openRawResource(csvResource);
            new LoadFilesTask().execute(inputStream);
        }

        return v;
    }

    private class LoadFilesTask extends AsyncTask<InputStream, Integer, Long> {

        private ArrayList<String> original_list = new ArrayList<>();

        protected Long doInBackground(InputStream... str) {
            long lineNumber = 0;
            InputStreamReader inputStreamReader;
            inputStreamReader = new
                    InputStreamReader(str[0]);
            Scanner inputStream = new Scanner(inputStreamReader);
            inputStream.nextLine(); // Ignores the first line

            while (inputStream.hasNext()) {
                lineNumber++;
                String[] line = inputStream.nextLine().split(","); // Splits the line up into a string array
                if (line.length == 10 && line[8].matches("-?\\d+(\\.\\d+)?"))
                    original_list.add(line[8] + " " + line[4] + " " + line[5]);
            }

            inputStream.close();
            return lineNumber;
        }

        //If you need to show the progress use this method
        protected void onProgressUpdate(Integer... progress) {

        }

        //This method is triggered at the end of the process, in your case when the loading has finished
        protected void onPostExecute(Long result) {
            vehicles_list.addAll(original_list);
            search.setOriginalData(vehicles_list);
        }
    }


}
