package es.ujaen.labtelema.practica1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import data.UserData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAuth.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAuth#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAuth extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private UserData userData;

    private OnFragmentInteractionListener mListener;

    public FragmentAuth() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAuth.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAuth newInstance(String param1, String param2) {
        FragmentAuth fragment = new FragmentAuth();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_fragment_auth, container, false);

        final EditText user = fragment.findViewById(R.id.fragment_auth_edit_name);
        final EditText pass = fragment.findViewById(R.id.fragment_auth_edit_pass);
        final EditText domain = fragment.findViewById(R.id.fragment_auth_edit_ip);
        final EditText port = fragment.findViewById(R.id.fragment_auth_edit_port);

        Button connect = fragment.findViewById(R.id.fragment_auth_button);


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_user = user.getEditableText().toString();
                String s_pass = pass.getEditableText().toString();
                String s_domain = domain.getEditableText().toString();
                String s_port = port.getEditableText().toString();

                short temp = 0;

                try {
                    temp = Short.parseShort(s_port);
                } catch (NumberFormatException ex) {
                    temp = 80;
                }
                userData = new UserData(s_user, s_pass, s_domain, temp);
                //Toast.makeText(getActivity(), s_user + " " + s_pass + " " + s_domain + " " + s_port, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(),ServiceActivity.class);
                intent.putExtra(ServiceActivity.PARAMETER_USER,s_user);
                intent.putExtra("pass",s_pass);
                intent.putExtra("domain",s_domain);
                intent.putExtra("port",s_port);
                //startActivity(intent);
                mListener.onFragmentInteraction(userData);
            }
        });

        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(UserData udata);
    }
}
