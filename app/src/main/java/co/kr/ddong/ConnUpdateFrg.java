package co.kr.ddong;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ConnUpdateFrg extends Fragment {
    LoginActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (LoginActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(String.valueOf(this), "aaaaaaaaaaaaaaaaa");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.conn_update, container, false);
        Button updateBt;
        updateBt = (Button) rootView.findViewById(R.id.updateVersion);
        Log.d(String.valueOf(this), "66666666666");

        updateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(String.valueOf(this), "7777777777");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.d(String.valueOf(this), "88888888888");
                intent.setData(Uri.parse("market://details?id=co.kr.ddong&hl=en-US&ah=FCSk0q07NZNsREPtF2cEiwBAue4"));
                Log.d(String.valueOf(this), "999999999");
                startActivity(intent);
                Log.d(String.valueOf(this), "0000000000000");
            }
        });
        return rootView;
    }
}
