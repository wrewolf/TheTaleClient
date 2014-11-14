package com.lonebytesoft.thetaleclient.service;

import com.lonebytesoft.thetaleclient.api.request.TakeCardRequest;
import com.lonebytesoft.thetaleclient.api.response.GameInfoResponse;
import com.lonebytesoft.thetaleclient.util.PreferencesManager;

/**
 * @author Hamster
 * @since 14.11.2014
 */
public class CardTaker implements GameStateWatcher {

    @Override
    public void processGameState(GameInfoResponse gameInfoResponse) {
        if(PreferencesManager.shouldAutoactionCardTake()) {
            if(gameInfoResponse.account.hero.basicInfo.cardHelpCurrent >= gameInfoResponse.account.hero.basicInfo.cardHelpTotal) {
                new TakeCardRequest(gameInfoResponse.account.accountId).execute(null);
            }
        }
    }

}
