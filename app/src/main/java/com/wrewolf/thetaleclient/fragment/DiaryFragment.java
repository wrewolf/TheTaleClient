package com.wrewolf.thetaleclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wrewolf.thetaleclient.DataViewMode;
import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.model.DiaryEntry;
import com.wrewolf.thetaleclient.api.request.GameInfoRequest;
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

        final ApiResponseCallback<GameInfoResponse> callback = RequestUtils.wrapCallback(new ApiResponseCallback<GameInfoResponse>() {
            @Override
            public void processResponse(GameInfoResponse response) {
                diaryContainer.removeAllViews();
                for(int i = response.account.hero.diary.size() - 1; i >= 0; i--) {
                    final DiaryEntry diaryEntry = response.account.hero.diary.get(i);
                    final View diaryEntryView = layoutInflater.inflate(R.layout.item_diary, diaryContainer, false);
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_place),
                            diaryEntry.place);
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_time),
                            String.format("%s %s", diaryEntry.time, diaryEntry.date));
                    UiUtils.setText(
                            diaryEntryView.findViewById(R.id.diary_text),
                            diaryEntry.text);
                    diaryContainer.addView(diaryEntryView);
                }
                setMode(DataViewMode.DATA);
            }

            @Override
            public void processError(GameInfoResponse response) {
                setError(response.errorMessage);
            }
        }, this);

        final int watchingAccountId = PreferencesManager.getWatchingAccountId();
        if(watchingAccountId == 0) {
            new GameInfoRequest(true).execute(callback, true);
        } else {
            new GameInfoRequest(true).execute(watchingAccountId, callback, true);
        }
    }

}
