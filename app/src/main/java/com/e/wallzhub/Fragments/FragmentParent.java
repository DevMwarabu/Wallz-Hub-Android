package com.e.wallzhub.Fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ActionMenuView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.e.wallzhub.Constants.Adapters.ViewPagerAdapter;
import com.e.wallzhub.Constants.Models.Advert;
import com.e.wallzhub.Dashbaord.AboutUs;
import com.e.wallzhub.Dashbaord.Dashboard;
import com.e.wallzhub.R;
import com.e.wallzhub.Searching;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class FragmentParent extends Fragment {
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private View mView;
    private List<Advert> adverts;
    private ViewPagerAdapter adapter;
    private Toolbar mToolbar;
    private ActionMenuView actionMenuView;
    private SearchView mSearchView;
    private InterstitialAd mInterstitialAd;
    private CardView mGo;
    private ReviewManager reviewManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_parent, container, false);

        reviewManager = ReviewManagerFactory.create(getContext());


        adapter = new ViewPagerAdapter(getFragmentManager(), getContext());
        adverts = new ArrayList<>();

        mViewPager = mView.findViewById(R.id.viewPager);
        tabLayout = mView.findViewById(R.id.tablayout_main);
        mToolbar = mView.findViewById(R.id.toolbar_main);
        mSearchView = mView.findViewById(R.id.searchview);
        mGo = mView.findViewById(R.id.card_go);
        actionMenuView = mView.findViewById(R.id.amvMenu);

        loadInterstitial();

        setHasOptionsMenu(true);

        //setting toolbar
        ((AppCompatActivity) getActivity()).getSupportActionBar();
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowTitleEnabled(false);

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(9);


        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        mGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mSearchView.getQuery().toString())) {
                    startActivity(new Intent(getContext(), Searching.class).putExtra("title", mSearchView.getQuery().toString()));
                } else {
                    Toast.makeText(getContext(), "Enter something to search..", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener) new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

       // loadSlides();


        return mView;
    }



    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(), getResources().getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dashboard, actionMenuView.getMenu());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        menuClicks(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void menuClicks(int r) {
        switch (r) {
            case R.id.nav_share:
                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            String shareBody = "Hi! Update your Gallery Photos using Wallz Hub App, for more information please click this link: https://play.google.com/store/apps/details?id=com.e.wallzhub";
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                            loadInterstitial();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "Hi! Update your Gallery Photos using Wallz Hub App, for more information please click this link: https://play.google.com/store/apps/details?id=com.e.wallzhub";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
                break;
            case R.id.nav_rate:
                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            showRateApp();
                            loadInterstitial();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    showRateApp();
                }
                break;
            case R.id.nav_more:
                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            openDeveloperStore();
                            loadInterstitial();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    openDeveloperStore();
                }
                break;
            case R.id.nav_facebook:
                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            openSocial("https://www.facebook.com/wallz.hub");
                            loadInterstitial();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    openSocial("https://www.facebook.com/wallz.hub");
                }
                break;
            case R.id.nav_instagram:
                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            openSocial("https://www.instagram.com/wallz_hub/");
                            loadInterstitial();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    openSocial("https://www.instagram.com/wallz_hub/");
                }
                break;
            case R.id.nav_twitter:
                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            openSocial("https://www.twitter.com/wallzhub");
                            loadInterstitial();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    openSocial("https://www.twitter.com/wallzhub");
                }
                break;
            case R.id.nav_policy:
                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) getActivity());
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            openAbout();
                            loadInterstitial();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            loadInterstitial();
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    openAbout();
                }
                break;
        }
    }

    private void openAbout() {
        startActivity(new Intent(getContext(), AboutUs.class));
    }


    private void openPlayStore() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getContext().getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow((Activity) getContext(), reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
                showRateAppFallbackDialog();
            }
        });
    }

    /**
     * Showing native dialog with three buttons to review the app
     * Redirect user to playstore to review the app
     */
    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Rate Us")
                .setMessage("If you enjoy our App. Please take a momment to rate it on playstore.")
                .setPositiveButton("RATE NOW", (dialog, which) -> {
                    openPlayStore();

                })
                .setNeutralButton("NOT NOW",
                        (dialog, which) -> {
                            dialog.dismiss();
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }

    private void openDeveloperStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=ClemMwa")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=ClemMwa")));
        }
    }

    private void openSocial(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

//    private void loadSlides() {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    Log.i("res_beer", "[" + response + "]");
//
//                    JSONArray jsonArray = response.getJSONArray("photos");
//
//                    if (shuffleJsonArray(jsonArray).length() > 0) {
//
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                            String photographer = jsonObject.getString("photographer");
//                            String photographer_url = jsonObject.getString("photographer_url");
//                            String id = jsonObject.getString("id");
//                            JSONObject src = jsonObject.getJSONObject("src");
//                            String imageUrl = src.getString("landscape");
//
//                            //adding to list
//                            Advert advert = new Advert(photographer, photographer_url, imageUrl);
//                            adverts.add(advert);
//                            //notifyadapter changes
//                            sliderView.setSliderAdapter(sliderAdapter, true);
//
//
//                        }
//                        adapter.notifyDataSetChanged();
//                    } else {
//                        //nothing found
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.i("res_beer", "[" + e.getMessage() + "]");
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("res_beer", "[" + error.getMessage() + "]");
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> map = new HashMap<String, String>();
//                map.put("authorization", Constants.api_key);
//                map.put("x-rapidapi-key", "d498088a25msheae6bb5b8a6fc9fp1c3886jsn5cef9e372445");
//                map.put("x-rapidapi-host", "PexelsdimasV1.p.rapidapi.com");
//                return map;
//            }
//        };
//        //creating a request queue
//        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
//        //adding the string request to request queue
//        requestQueue.add(jsonObjectRequest);
//
//    }


    public static JSONArray shuffleJsonArray(JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--) {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    public void addPage(String collection) {
        Bundle bundle = new Bundle();
        bundle.putString("title", collection);
        FragmentChild fragmentChild = new FragmentChild();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, collection);
        adapter.notifyDataSetChanged();
        for (int i = 0; i < Dashboard.collectionsMain.size(); i++) {
            tabLayout.addTab(tabLayout.newTab());
            tabLayout.getTabAt(i).setText(Dashboard.collectionsMain.get(i).getCollection());
        }
        mViewPager.setCurrentItem(0);
    }
}