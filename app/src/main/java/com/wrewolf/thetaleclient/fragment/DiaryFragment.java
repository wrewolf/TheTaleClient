package com.wrewolf.thetaleclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrewolf.thetaleclient.DataViewMode;
import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.model.DiaryEntry;
import com.wrewolf.thetaleclient.api.request.DiaryRequest;
import com.wrewolf.thetaleclient.api.request.GameInfoRequest;
import com.wrewolf.thetaleclient.api.response.DiaryResponse;
import com.wrewolf.thetaleclient.api.response.GameInfoResponse;
import com.wrewolf.thetaleclient.util.PreferencesManager;
import com.wrewolf.thetaleclient.util.RequestUtils;
import com.wrewolf.thetaleclient.util.UiUtils;

/**
 * @author Hamster
 * @since 06.10.2014
 */
public class DiaryFragment extends WrapperFragment {

    private LayoutInflater layoutInflater;

  private ViewGroup diaryContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutInflater = inflater;
      View rootView = inflater.inflate(R.layout.fragment_diary, container, false);

        diaryContainer = (ViewGroup) rootView.findViewById(R.id.diary_container);

        return wrapView(layoutInflater, rootView);
    }

    @Override
    public void refresh(final boolean isGlobal) {
        super.refresh(isGlobal);

        final ApiResponseCallback<DiaryResponse> callback = RequestUtils.wrapCallback(new ApiResponseCallback<DiaryResponse>() {
            @Override
            public void processResponse(DiaryResponse response) {
                diaryContainer.removeAllViews();

                for(int i = response.diary.size() - 1; i >= 0; i--) {
                    final DiaryEntry diaryEntry = response.diary.get(i);
                    final View diaryEntryView = layoutInflater.inflate(R.layout.item_diary, diaryContainer, false);
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_place),
                            diaryEntry.position);
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_time),
                            String.format("%s %s", diaryEntry.game_time, diaryEntry.game_date));
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_text),
                            diaryEntry.message);
                    diaryContainer.addView(diaryEntryView);
                }
                setMode(DataViewMode.DATA);
            }

            @Override
            public void processError(DiaryResponse response) {
                setError(response.errorMessage);
            }
        }, this);

            new DiaryRequest().execute(callback);
    }

}
