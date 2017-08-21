package com.example.earth.fuelfriend;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;


public class SearchFragment extends Fragment {

    private SearchableAdapter search;
    ArrayList<String> lv_vehiclelist = new ArrayList<>();
    ArrayList<String> databaseVehicles = new ArrayList<>();
    ArrayList<String> progress = new ArrayList<>();
    ListView lv;
    DBHelper dbHelper;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int csvResources[] = {R.raw.vehicles_pt6, R.raw.vehicles_pt5, R.raw.vehicles_pt2, R.raw.vehicles_pt3, R.raw.vehicles_pt4, R.raw.vehicles_pt1};

        if (savedInstanceState != null) {
            lv_vehiclelist = savedInstanceState.getStringArrayList("lv_vehiclelist");
            databaseVehicles = savedInstanceState.getStringArrayList("databaseVehicles");
            progress = savedInstanceState.getStringArrayList("progress");
        }


        final View v = inflater.inflate(R.layout.search_transport, container, false);
        dbHelper = new DBHelper(getContext());
        search = new SearchableAdapter(getContext(), lv_vehiclelist);
        SearchView sv = (SearchView) v.findViewById(R.id.search_input);
        lv = (ListView) v.findViewById(R.id.transport_list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CarProfileFragment cpf = new CarProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data", databaseVehicles.get(i));
                cpf.setArguments(bundle);

                FragmentManager fragmentManager = getFm();
                fragmentManager.beginTransaction().replace(R.id.content_frame, cpf, "search").commit(); // lol change this
                // When clicked, pass information to another fragment which displays information on the car
                // and display a 'Add' button to add to personal database of cars which the user drives.
            }
        });

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
            if (!progress.contains(Integer.toString(csvResource))) {
            InputStream inputStream = getResources().openRawResource(csvResource);
                new LoadCSVTask(csvResource).execute(inputStream);
            }
        }

        return v;
    }

    public FragmentManager getFm() {
        return getFragmentManager();
    }

    public void hide() {
        getFragmentManager().beginTransaction().hide(this).commit();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        System.out.println("SAVED STATE");
        outState.putStringArrayList("progress", progress);
        outState.putStringArrayList("databaseVehicles", databaseVehicles);
        outState.putStringArrayList("lv_vehiclelist", lv_vehiclelist);
        super.onSaveInstanceState(outState);
    }

    private class LoadCSVTask extends AsyncTask<InputStream, Integer, Long> {

        private int file_id;
        private ArrayList<String> original_list = new ArrayList<>(), readingVehicleFile = new ArrayList<>();

        public LoadCSVTask(int id) {
            file_id = id;
        }

        protected Long doInBackground(InputStream... str) {
            long lineNumber = 0;
            InputStreamReader inputStreamReader;
            inputStreamReader = new
                    InputStreamReader(str[0]);
            Scanner inputStream = new Scanner(inputStreamReader);
            inputStream.nextLine(); // Ignores the first line

            while (inputStream.hasNext()) {
                lineNumber++;
                String nextLine = inputStream.nextLine();
                String[] line = nextLine.split(","); // Splits the line up into a string array
                if (line.length == 10 && line[8].matches("-?\\d+(\\.\\d+)?")) {
                    original_list.add(line[8] + " " + line[4] + " " + line[5]);
                    readingVehicleFile.add(nextLine);
                }

            }
            progress.add(Integer.toString(file_id));
            inputStream.close();
            return lineNumber;
        }

        //If you need to show the progress use this method
        protected void onProgressUpdate(Integer... progress) {

        }

        //This method is triggered at the end of the process, in your case when the loading has finished
        protected void onPostExecute(Long result) {

            databaseVehicles.addAll(readingVehicleFile);
            lv_vehiclelist.addAll(original_list);
            search.setOriginalData(lv_vehiclelist);
        }
    }


}
