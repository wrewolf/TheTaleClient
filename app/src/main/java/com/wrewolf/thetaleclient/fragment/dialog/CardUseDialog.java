package com.wrewolf.thetaleclient.fragment.dialog;

import android.app.ProgressDialog;
import android.graphics.Path;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.api.ApiResponseCallback;
import com.wrewolf.thetaleclient.api.dictionary.Archetype;
import com.wrewolf.thetaleclient.api.dictionary.CardFullType;
import com.wrewolf.thetaleclient.api.dictionary.CardTargetType;
import com.wrewolf.thetaleclient.api.dictionary.CardType;
import com.wrewolf.thetaleclient.api.dictionary.EnergyRegeneration;
import com.wrewolf.thetaleclient.api.model.CardInfo;
import com.wrewolf.thetaleclient.api.model.CouncilMemberInfo;
import com.wrewolf.thetaleclient.api.model.PlaceInfo;
import com.wrewolf.thetaleclient.api.request.PlaceRequest;
import com.wrewolf.thetaleclient.api.request.PlacesRequest;
import com.wrewolf.thetaleclient.api.request.UseCardRequest;
import com.wrewolf.thetaleclient.api.response.CommonResponse;
import com.wrewolf.thetaleclient.api.response.PlaceResponse;
import com.wrewolf.thetaleclient.api.response.PlacesResponse;
import com.wrewolf.thetaleclient.util.DialogUtils;
import com.wrewolf.thetaleclient.util.ObjectUtils;
import com.wrewolf.thetaleclient.util.RequestUtils;
import com.wrewolf.thetaleclient.util.UiUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Hamster
 * @since 03.05.2015
 */
public class CardUseDialog extends BaseDialog {

    private static final String PARAM_TITLE = "PARAM_TITLE";
    private static final String PARAM_CARD = "PARAM_CARD";

    private Runnable onSuccess;
    
    private List<PlaceInfo> places;
    private List<Archetype> archetypes;
    private List<EnergyRegeneration> energyRegenerations;
    private Map<Integer, List<CouncilMemberInfo>> persons;
    private CardInfo card;

    private View viewAction;
    private TextView textPlace;
    private TextView textPerson;

    public static CardUseDialog newInstance(final String title, final CardInfo card) {
        final CardUseDialog dialog = new CardUseDialog();

        final Bundle args = new Bundle();
        args.putString(PARAM_TITLE, title);
        args.putParcelable(PARAM_CARD, card);

        dialog.setArguments(args);
        return dialog;
    }

    public void setOnSuccessListener(final Runnable listener) {
        this.onSuccess = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_content_card_use, container, false);

        card = getArguments().getParcelable(PARAM_CARD);
        final Spannable cardName = new SpannableString(card.name);
        cardName.setSpan(new ForegroundColorSpan(getResources().getColor(card.type.getRarity().getColorResId())),
                0, cardName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        UiUtils.setText(view.findViewById(R.id.dialog_card_use_name), cardName);
        UiUtils.setText(view.findViewById(R.id.dialog_card_use_description), card.type.getDescription());

        persons = new HashMap<>();
        final boolean isPlacePresent = isPlacePresent(card.type.getTargetType(card.fullType));
        final boolean isPersonPresent = isPersonPresent(card.type.getTargetType(card.fullType));

        viewAction = view.findViewById(R.id.dialog_card_use_action);
      View blockPlace = view.findViewById(R.id.dialog_card_use_place_block);
      View blockPerson = view.findViewById(R.id.dialog_card_use_person_block);
        textPlace = (TextView) view.findViewById(R.id.dialog_card_use_place);
        textPerson = (TextView) view.findViewById(R.id.dialog_card_use_person);

        if(isPlacePresent) {
            viewAction.setEnabled(false);

            blockPlace.setVisibility(View.VISIBLE);
            textPlace.setEnabled(false);
            textPlace.setText(getString(R.string.common_loading));

            if(isPersonPresent) {
                blockPerson.setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.dialog_card_use_person_title)).setText(getString(
                        card.type.getTargetType(card.fullType) == CardTargetType.BUILDING
                                ? R.string.game_card_use_building
                                : R.string.game_card_use_person));
                textPerson.setEnabled(false);
                textPerson.setText(getString(R.string.common_loading));
            } else {
                blockPerson.setVisibility(View.GONE);
            }

            new PlacesRequest().execute(RequestUtils.wrapCallback(new ApiResponseCallback<PlacesResponse>() {
                @Override
                public void processResponse(final PlacesResponse response) {
                    places = response.places;
                    Collections.sort(places, new Comparator<PlaceInfo>() {
                        @Override
                        public int compare(PlaceInfo lhs, PlaceInfo rhs) {
                            return lhs.name.compareTo(rhs.name);
                        }
                    });
                    final String[] placeNames;

                    if (canBeForgotten(card.type, card.fullType)) {
                        final int count = places.size() + 1;
                        placeNames = new String[count];
                        placeNames[0] = getString(R.string.game_cards_forget);
                        for(int i = 1; i < count; i++) {
                            placeNames[i] = places.get(i - 1).name;
                        }
                    } else {
                        final int count = places.size();
                        placeNames = new String[count];
                        for(int i = 0; i < count; i++) {
                            placeNames[i] = places.get(i).name;
                        }
                    }

                    textPlace.setEnabled(true);
                    onPlaceSelected(0);
                    textPlace.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogUtils.showChoiceDialog(
                                    getChildFragmentManager(),
                                    getString(R.string.game_card_use_place),
                                    placeNames,
                                    new ChoiceDialog.ItemChooseListener() {
                                        @Override
                                        public void onItemSelected(int position) {
                                            onPlaceSelected(position);
                                        }
                                    });
                        }
                    });
                }

                @Override
                public void processError(PlacesResponse response) {
                    textPlace.setText(getString(R.string.common_error_hint));
                    if(isPersonPresent) {
                        textPerson.setText(getString(R.string.common_error_hint));
                    }
                }
            }, this));
        } else if (card.type.getTargetType(card.fullType) == CardTargetType.ENERGY_REGENERATION) {
            viewAction.setEnabled(false);

            blockPlace.setVisibility(View.VISIBLE);
            textPlace.setEnabled(false);
            textPlace.setText(getString(R.string.common_loading));

            energyRegenerations = Arrays.asList(EnergyRegeneration.values());

            final int count = energyRegenerations.size();
            final String[] energyRegenerationNames = new String[count];
            for (int i = 0; i < count; i++) {
                energyRegenerationNames[i] = energyRegenerations.get(i).getCode();
            }

            textPlace.setEnabled(true);
            onEnergyRegenerationSelected(0);

            textPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showChoiceDialog(
                            getChildFragmentManager(),
                            getString(R.string.game_card_use_energy_regeneration),
                            energyRegenerationNames,
                            new ChoiceDialog.ItemChooseListener() {
                                @Override
                                public void onItemSelected(int position) {
                                    onEnergyRegenerationSelected(position);
                                }
                            }
                    );
                }
            });
        } else if (card.type.getTargetType(card.fullType) == CardTargetType.ARCHETYPE) {
            viewAction.setEnabled(false);

            blockPlace.setVisibility(View.VISIBLE);
            textPlace.setEnabled(false);
            textPlace.setText(getString(R.string.common_loading));

            archetypes = Arrays.asList(Archetype.values());

            final int count = archetypes.size();
            final String[] archetypeNames = new String[count];
            for (int i = 0; i < count; i++) {
                archetypeNames[i] = archetypes.get(i).getCode();
            }

            textPlace.setEnabled(true);
            onArchetypeSelected(0);

            textPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogUtils.showChoiceDialog(
                        getChildFragmentManager(),
                        getString(R.string.game_card_use_archetype),
                        archetypeNames,
                        new ChoiceDialog.ItemChooseListener() {
                            @Override
                            public void onItemSelected(int position) {
                                onArchetypeSelected(position);
                            }
                        }
                    );
                }
            });
        } else {
            blockPlace.setVisibility(View.GONE);
            blockPerson.setVisibility(View.GONE);
            viewAction.setEnabled(true);
            viewAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                            getString(R.string.game_card_use),
                            getString(R.string.game_card_use_progress),
                            true, false);
                    new UseCardRequest().execute(card.id, getCardUseCallback(progressDialog));
                }
            });
        }

        return wrapView(inflater, view, getArguments().getString(PARAM_TITLE));
    }

    private boolean isPlacePresent(final CardTargetType targetType) {
        return (targetType == CardTargetType.PLACE)
                || (targetType == CardTargetType.PERSON)
                || (targetType == CardTargetType.BUILDING);
    }

    private boolean isPersonPresent(final CardTargetType targetType) {
        return (targetType == CardTargetType.PERSON)
                || (targetType == CardTargetType.BUILDING);
    }

    private boolean canBeForgotten(final CardType cardType, final String fullType) {
        final CardFullType cardFullType = ObjectUtils.getEnumForCode(CardFullType.class, fullType);
        if (cardFullType == null) {
            return false;
        } else {
            return cardFullType.getCanBeForgotten();
        }
    }

    private void onEnergyRegenerationSelected(final int energyRegenerationIndex) {
        final EnergyRegeneration energyRegeneration = energyRegenerations.get(energyRegenerationIndex);
        textPlace.setText(energyRegeneration.getCode());

        viewAction.setEnabled(true);
        viewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                        getString(R.string.game_card_use),
                        getString(R.string.game_card_use_progress),
                        true, false);
                new UseCardRequest().execute(
                        card.id, energyRegeneration.getValue(),
                        getCardUseCallback(progressDialog));
            }
        });
    }

    private void onArchetypeSelected(final int archetypeIndex) {
        final Archetype archetype = archetypes.get(archetypeIndex);
        textPlace.setText(archetype.getCode());

        viewAction.setEnabled(true);
        viewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                        getString(R.string.game_card_use),
                        getString(R.string.game_card_use_progress),
                        true, false);
                new UseCardRequest().execute(
                        card.id, archetype.getValue(),
                        getCardUseCallback(progressDialog));
            }
        });
    }

    private void onPlaceSelected(final int placeIndex) {
        final String value;
        if (canBeForgotten(card.type, card.fullType)) {
            if (placeIndex == 0) {
                value = null;
                textPlace.setText(getString(R.string.game_cards_forget));
            } else {
                final PlaceInfo place = places.get(placeIndex - 1);
                value = String.valueOf(place.id);
                textPlace.setText(place.name);
            }
        } else {
            final PlaceInfo place = places.get(placeIndex);
            value = String.valueOf(place.id);
            textPlace.setText(place.name);
        }
        if(isPersonPresent(card.type.getTargetType(card.fullType))) {
            textPerson.setEnabled(false);
            if (canBeForgotten(card.type, card.fullType) && placeIndex == 0) {
                viewAction.setEnabled(true);
                textPerson.setText(getString(R.string.game_cards_forget));
                viewAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                                getString(R.string.game_card_use),
                                getString(R.string.game_card_use_progress),
                                true, false);
                        new UseCardRequest().execute(
                                card.id, value,
                                getCardUseCallback(progressDialog));
                    }
                });
            } else {
                viewAction.setEnabled(false);
                final List<CouncilMemberInfo> council = persons.get(placeIndex);
                if(council == null) {
                    textPerson.setText(getString(R.string.common_loading));
                    new PlaceRequest(value).execute(RequestUtils.wrapCallback(new ApiResponseCallback<PlaceResponse>() {
                        @Override
                        public void processResponse(PlaceResponse response) {
                            if(card.type.getTargetType(card.fullType) == CardTargetType.BUILDING) {
                                for(final Iterator<CouncilMemberInfo> councilIterator = response.council.iterator();
                                    councilIterator.hasNext();) {
                                    if(councilIterator.next().buildingId == null) {
                                        councilIterator.remove();
                                    }
                                }
                            }
                            Collections.sort(response.council, new Comparator<CouncilMemberInfo>() {
                                @Override
                                public int compare(CouncilMemberInfo lhs, CouncilMemberInfo rhs) {
                                    return Double.compare(rhs.power, lhs.power);
                                }
                            });
                            persons.put(placeIndex, response.council);
                            fillPersons(placeIndex);
                        }

                        @Override
                        public void processError(PlaceResponse response) {
                            textPerson.setText(getString(R.string.common_error_hint));
                        }
                    }, this));
                } else {
                    fillPersons(placeIndex);
                }
            }
        } else {
            viewAction.setEnabled(true);
            viewAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                            getString(R.string.game_card_use),
                            getString(R.string.game_card_use_progress),
                            true, false);
                    new UseCardRequest().execute(
                            card.id, value,
                            getCardUseCallback(progressDialog));
                }
            });
        }
    }

    private void fillPersons(final int placeIndex) {
        final List<CouncilMemberInfo> council = persons.get(placeIndex);
        final int personsCount = council.size();
        if(personsCount == 0) {
            textPerson.setEnabled(false);
            textPerson.setText(getString(R.string.game_card_use_no_buildings));
            return;
        }

        textPerson.setEnabled(true);
        final String[] personNames = new String[personsCount];
        for(int i = 0; i < personsCount; i++) {
            personNames[i] = council.get(i).name;
        }

        onPersonSelected(placeIndex, 0);
        textPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtils.showChoiceDialog(
                        getChildFragmentManager(),
                        getString(card.type.getTargetType(card.fullType) == CardTargetType.BUILDING
                                ? R.string.game_card_use_building : R.string.game_card_use_person),
                        personNames,
                        new ChoiceDialog.ItemChooseListener() {
                            @Override
                            public void onItemSelected(int position) {
                                onPersonSelected(placeIndex, position);
                            }
                        });
            }
        });
    }

    private void onPersonSelected(final int placeIndex, final int personIndex) {
        final CouncilMemberInfo councilMemberInfo = persons.get(placeIndex).get(personIndex);
        textPerson.setText(councilMemberInfo.name);
        viewAction.setEnabled(true);
        viewAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                        getString(R.string.game_card_use),
                        getString(R.string.game_card_use_progress),
                        true, false);
                new UseCardRequest().execute(
                        card.id,
                        card.type.getTargetType(card.fullType) == CardTargetType.BUILDING
                                ? String.valueOf(councilMemberInfo.buildingId) : String.valueOf(councilMemberInfo.id),
                        getCardUseCallback(progressDialog));
            }
        });
    }

    private ApiResponseCallback<CommonResponse> getCardUseCallback(final ProgressDialog progressDialog) {
        return RequestUtils.wrapCallback(new ApiResponseCallback<CommonResponse>() {
            @Override
            public void processResponse(CommonResponse response) {
                progressDialog.dismiss();
                dismiss();
                if(onSuccess != null) {
                    onSuccess.run();
                }
            }

            @Override
            public void processError(CommonResponse response) {
                progressDialog.dismiss();
                DialogUtils.showCommonErrorDialog(getActivity().getSupportFragmentManager(), getActivity());
                dismiss();
            }
        }, CardUseDialog.this);
    }

}
