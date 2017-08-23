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

    ArrayList<String> mDatabaseVehicles = new ArrayList<>();
    ArrayList<String> mProgress = new ArrayList<>();
    ListView lv;
    DBHelper dbHelper;
    private SearchableAdapter mSearchAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int csvResources[] = {R.raw.vehicles_pt6, R.raw.vehicles_pt5, R.raw.vehicles_pt2/*, R.raw.vehicles_pt3, R.raw.vehicles_pt4, R.raw.vehicles_pt1*/};

        if (savedInstanceState != null) {
            mDatabaseVehicles = savedInstanceState.getStringArrayList("mDatabaseVehicles");
            mProgress = savedInstanceState.getStringArrayList("mProgress");
        }

        final View v = inflater.inflate(R.layout.search_transport, container, false);
        dbHelper = new DBHelper(getContext());
        mSearchAdapter = new SearchableAdapter(getContext(), mDatabaseVehicles);
        SearchView sv = (SearchView) v.findViewById(R.id.search_input);
        lv = (ListView) v.findViewById(R.id.transport_list);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                GeneralHelper.hideKeyboard(getActivity());
                CarProfileFragment cpf = new CarProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("data", (String) view.getTag(R.id.string));
                cpf.setArguments(bundle);

                FragmentManager fragmentManager = getFm();
                fragmentManager.beginTransaction().replace(R.id.content_frame, cpf, "mSearchAdapter").commit(); // lol change this
                // When clicked, pass information to another fragment which displays information on the car
                // and display a 'Add' button to add to personal database of cars which the user drives.
            }
        });

        lv.setAdapter(mSearchAdapter);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mSearchAdapter.getFilter().filter(s);
                return false;
            }
        });

        for (int csvResource : csvResources) {
            if (!mProgress.contains(Integer.toString(csvResource))) {
            InputStream inputStream = getResources().openRawResource(csvResource);
                new LoadCSVTask(csvResource).execute(inputStream);
            }
        }

        return v;
    }

    public FragmentManager getFm() {
        return getFragmentManager();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putStringArrayList("mProgress", mProgress);
        outState.putStringArrayList("mDatabaseVehicles", mDatabaseVehicles);
        super.onSaveInstanceState(outState);
    }

    private class LoadCSVTask extends AsyncTask<InputStream, Integer, Long> {

        private int file_id;
        private ArrayList<String> readingVehicleFile = new ArrayList<>();

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
                String[] line = nextLine.split(",");

                if (line.length == 10 && line[8].matches("-?\\d+(\\.\\d+)?") && !readingVehicleFile.contains(nextLine)) // data set had duplicates..
                    readingVehicleFile.add(nextLine);
            }
            mProgress.add(Integer.toString(file_id));
            inputStream.close();
            return lineNumber;
        }

        //If you need to show the mProgress use this method
        protected void onProgressUpdate(Integer... progress) {

        }

        //This method is triggered at the end of the process, in your case when the loading has finished
        protected void onPostExecute(Long result) {
            mDatabaseVehicles.addAll(readingVehicleFile);
            mSearchAdapter.setOriginalData(mDatabaseVehicles);
        }
    }


}
