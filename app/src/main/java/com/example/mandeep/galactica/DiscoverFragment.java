package com.example.mandeep.galactica;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mandeep.galactica.movies.MovieInterestsModel;
import com.example.mandeep.galactica.music.MusicInterestsModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


public class DiscoverFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener {

    List<Person> discoveredPeople = new ArrayList<>();
    DiscoveredPeopleAdapter adapter;

    Context context;
    TabLayout tabsTitleStrip;
    public static final String TITLE = "Discover";

    GoogleApiClient device;
    String myName = null;

    public DiscoverFragment() { }

    public static DiscoverFragment newInstance() {
        return new DiscoverFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        device = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .setAccountName("Galactica")
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        tabsTitleStrip = (TabLayout) ((MainActivity) context).findViewById(R.id.tabs);
        tabsTitleStrip.setVisibility(View.GONE);
    }

    @Bind(R.id.discover) Button discover;
    @Bind(R.id.listOfSimilarMovies) EditText listOfSimilarMovies;
    @Bind(R.id.discoveredPeopleList) ListView discoveredPeopleView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.discover_fragment, container, false);
        ButterKnife.bind(this, v);


        adapter = new DiscoveredPeopleAdapter(context, discoveredPeople);
        discoveredPeopleView.setAdapter(adapter);
        return v;
    }

    private void refreshList() {
        Collections.sort(discoveredPeople);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) context).setTitle(TITLE);

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });
        myName = UUID.randomUUID().toString().substring(0, 8);
        discoveredPeopleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((MainActivity) context)
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragmentHolder,
                            new MatchesFragment()
                                    .setMovieInterestList(discoveredPeople.get(position).getMovieInterests())
                                    .setMusicInterestList(discoveredPeople.get(position).getMusicInterests())
                    )
                    .commit();
            }
        });

        discoveredPeopleView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "Imagine you are now chatting!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (someInterestsInModel())
            device.connect();
        else
            Timber.e("onStart: Model empty. not advertising...");
    }

    private boolean someInterestsInModel() {
        return (!MovieInterestsModel.getInstance().getMovieIDs().isEmpty()) ||
                (!MusicInterestsModel.getInstance().getMusicIDs().isEmpty());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (device.isConnected()) {
            Nearby.Connections.stopAdvertising(device);
            device.disconnect();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Timber.i("Connected to Nearby Service");
        setup();
    }

    public void setup() {
        if (device.isConnected())
            startAdvertising();
        else
            Timber.e("Device not connected for advertising");
    }

    private void startAdvertising() {
        String serviceIdToBroadcast = getString(R.string.service_id);

        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(context.getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);
        long NO_TIMEOUT = 0L;

        Nearby.Connections.startAdvertising(device, serviceIdToBroadcast, appMetadata, NO_TIMEOUT,
                this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    Timber.i("Started Advertising");
                }
                else {
                    Timber.e("Error advertising %s", result.getStatus().getStatusMessage());
                }
            }
        });
    }

    private void startDiscovery() {

        if (!device.isConnected()) {
            Toast.makeText(context, "Add stuff to interests first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String serviceId = getString(R.string.service_id);
        long timeout = 1000L;

        Nearby.Connections.startDiscovery(device, serviceId, timeout, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Timber.i("Started discovery");
                }
                else {
                    Timber.e("Error starting discovery: %s", status.getStatusMessage());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionRequest(String remoteEndpointId, String remoteDeviceId, String remoteEndpointName, byte[] payload) {

        addToDiscoveredPeopleList(new String(payload));

        byte[] requestPayload = getConnectionRequestPayload().toString().getBytes();

        Nearby.Connections.acceptConnectionRequest(
                device,
                remoteEndpointId,
                requestPayload,
                this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) { }
                        else { }
                    }
                });

    }

    private void addToDiscoveredPeopleList(String payload) {

        List<Integer> movies;
        List<String> music;
        try {
            JSONObject obj = new JSONObject(payload);

            JSONArray moviesInPayload = (JSONArray) obj.get("mo");
            movies = new ArrayList();
            if (moviesInPayload != null) {
                int len = moviesInPayload.length();
                for (int i = 0; i < len; i++) {
                    movies.add(Integer.parseInt(moviesInPayload.get(i).toString()));
                }
            }

            JSONArray musicInPayload = (JSONArray) obj.get("mu");
            music = new ArrayList();
            if (musicInPayload != null) {
                int len = musicInPayload.length();
                for (int i = 0; i < len; i++) {
                    music.add(musicInPayload.get(i).toString());
                }
            }
            String name = obj.getString("n");

            discoveredPeople.add(new Person(name, movies, music));
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onEndpointFound(String endpointId, String deviceId, String serviceId, String name) {
        Timber.i("onEndpointFound() called with: "
                + "endpointId = [" + endpointId +
                "], deviceId = [" + deviceId
                + "], serviceId = [" + serviceId +
                "], name = [" + name + "]");
        try {
            connectTo(endpointId, name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onEndpointLost(String s) {

    }

    @Override
    public void onMessageReceived(String s, byte[] bytes, boolean b) {
        Timber.e("onMessageReceived:");
    }

    private void connectTo(String endpointId, final String endpointName) throws UnsupportedEncodingException {

        JSONObject requestPayload = getConnectionRequestPayload();
        Timber.e("connectTo: Sending %s", requestPayload.toString());
        byte[] myPayload = requestPayload.toString().getBytes();

        Nearby.Connections.sendConnectionRequest(device, myName, endpointId, myPayload, new Connections.ConnectionResponseCallback() {
            @Override
            public void onConnectionResponse(String remoteEndpointId, Status status, byte[] bytes) {
                if (status.isSuccess()) {

                    // todo endpoint name is not actually the name. Build in the name for the user.
                    Timber.i("Connected to %s successfully", endpointName);
                    addToDiscoveredPeopleList(new String(bytes));
                }
                else {
                    Timber.e("Error %s connecting to %s", status.getStatusMessage(), endpointName);
                }
            }
        }, this);
    }

    private JSONObject getConnectionRequestPayload() {

        JSONObject obj = new JSONObject();
        List<Integer> movies = MovieInterestsModel.getInstance().getMovieIDs();
        List<String> music = MusicInterestsModel.getInstance().getMusicIDs();
        try {
            obj.put("mo", new JSONArray(movies));
            obj.put("mu", new JSONArray(music));
            obj.put("n", myName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public void onDisconnected(String s) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
