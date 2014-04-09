package org.szuwest.lib;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import my.base.util.LogUtils;

public class BaseFragment extends Fragment{

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LogUtils.d(getClass().getSimpleName() , ":onAttach");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.d(getClass().getSimpleName() , ":onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtils.d(getClass().getSimpleName() , ":onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		LogUtils.d(getClass().getSimpleName() , ":onViewCreated");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtils.d(getClass().getSimpleName() , ":onActivityCreated");
	}
	
	@Override
	public void onStart() {
		LogUtils.d(getClass().getSimpleName() , ":onStart");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		LogUtils.d(getClass().getSimpleName() , ":onResume");
		super.onResume();
	}
	
	@Override
	public void onPause() {
		LogUtils.d(getClass().getSimpleName() , ":onPause");
		super.onPause();
	}
	
	@Override
	public void onStop() {
		LogUtils.d(getClass().getSimpleName() , ":onStop");
		super.onStop();
	}
	
	@Override
	public void onDestroyView() {
		LogUtils.d(getClass().getSimpleName() , ":onDestroyView");
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		LogUtils.d(getClass().getSimpleName() , ":onDestroy");
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		LogUtils.d(getClass().getSimpleName() , ":onDetach");
		super.onDetach();
	}
}
