package com.example.projectnt118.fragment;


//import static android.provider.CallLog.Locations.LONGITUDE;
import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMoveListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.androidbrowserhelper.locationdelegation.LocationProvider;
import com.mapbox.android.core.location.LocationEngine;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.projectnt118.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.bindgen.Expected;
//import com.mapbox.common.location.LocationProvider;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;

import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;

import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.MapboxNavigationProvider;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;

import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MapFragment extends Fragment {
    private MapView mapView;
    //private TextInputEditText searchBar;
    private FloatingActionButton focusLocationButton, zoomInButton, zoomOutButton, navigationButton, soundButton;
//    private SearchResultsView searchResultsView;
//    private SearchEngineUiAdapter searchEngineUiAdapter;
    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxRouteLineApi mapboxRouteLineApi;
    private MapboxRouteLineView mapboxRouteLineView;
    boolean focusLocation = true;
    private MapboxNavigation mapboxNavigation;
    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
            if (focusLocation) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }
        }
    };

    public RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            mapboxRouteLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {

                }
            });
        }
    };

    private void updateCamera(Point point, double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions options = new CameraOptions.Builder().center(point).zoom(18.0).bearing(bearing).pitch(45.0)
                .padding(new EdgeInsets(1000.0, 0.0, 0.0,0.0)).build();
        getCamera(mapView).easeTo(options, animationOptions);
    }




    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    });
    private final OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
        }
    };
    private final OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            focusLocation = false;
            mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).zoom(20.0).build());
            getGestures(mapView).setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
        }
    };
    private final OnMoveListener onMoveListener = new OnMoveListener() {

        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            getLocationComponent(mapView).removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
            getLocationComponent(mapView).removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
            getGestures(mapView).removeOnMoveListener(onMoveListener);

        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {

            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapboxNavigation = MapboxNavigationProvider.create(
                new NavigationOptions.Builder(getContext())
                        .accessToken(getString(R.string.mapbox_access_token))

                        .build());

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView);
        focusLocationButton = view.findViewById(R.id.my_location);
        zoomInButton = view.findViewById(R.id.zoom_in);
        zoomOutButton = view.findViewById(R.id.zoom_out);
        //searchBar = view.findViewById(R.id.searchET);
        navigationButton = view.findViewById(R.id.navigation);
        //searchResultsView = view.findViewById(R.id.search_results_view);
        String accessToken = getString(R.string.mapbox_access_token);


        MapboxRouteLineOptions options = new MapboxRouteLineOptions.Builder(requireContext()).withRouteLineResources(new RouteLineResources.Builder().build())
                .withRouteLineBelowLayerId("road-label").build();
        mapboxRouteLineApi = new MapboxRouteLineApi(options);
        mapboxRouteLineView = new MapboxRouteLineView(options);

        NavigationOptions navigationOptions = new NavigationOptions.Builder(requireContext()).accessToken(accessToken).build();
        MapboxNavigationApp.setup(navigationOptions);

        mapboxNavigation.registerRoutesObserver(routesObserver);
        mapboxNavigation.registerLocationObserver(locationObserver);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }

//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
//        }


        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            mapboxNavigation.startTripSession();
        }
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.setEnabled(true);
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_SHORT).show();
            }
        });
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());

                LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
                locationComponentPlugin.setEnabled(true);
//                LocationPuck2D locationPuck2D = new LocationPuck2D();
//                locationPuck2D.setBearingImage(ImageHolder.from(R.drawable.baseline_circle_24));
//                locationComponentPlugin.setLocationPuck(locationPuck2D);
                locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);
                getGestures(mapView).addOnMoveListener(onMoveListener);
                locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                    @Override
                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
                        locationComponentSettings.setEnabled(true);
                        locationComponentSettings.setPulsingEnabled(true);
                        return null;
                    }
                });
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_push_pin_24);
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
                addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        //pointAnnotationManager.deleteAll();
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_push_pin_24);
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER)
                                .withIconImage(bitmap).withPoint(point);
                        pointAnnotationManager.create(pointAnnotationOptions);
                        navigationButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fetchRoute(point);
                            }
                        });
                        return false;
                    }
                });


                focusLocationButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocation = true;
                        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
                        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
                        getGestures(mapView).addOnMoveListener(onMoveListener);

                    }
                });
                zoomInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(mapView.getMapboxMap().getCameraState().getZoom() + 1).build());
                    }
                });

                zoomOutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(mapView.getMapboxMap().getCameraState().getZoom() - 1).build());
                    }
                });

            }
        });

    }

//    private void addOnMoveListener(MapboxMap mapboxMap, OnMapClickListener onMapClickListener) {
//    }
    @SuppressLint("MissingPermission")
    private void fetchRoute(Point point) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext());
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                navigationButton.setEnabled(false);
                RouteOptions.Builder builder = RouteOptions.builder();
                Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                builder.coordinatesList(Arrays.asList(origin, point));
                builder.alternatives(false);
                builder.profile(DirectionsCriteria.PROFILE_DRIVING);
                builder.bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                        mapboxNavigation.setNavigationRoutes(list);
                        focusLocationButton.performClick();
                        navigationButton.setEnabled(true);

                    }

                    @Override
                    public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                        navigationButton.setEnabled(true);
                        Toast.makeText(getContext(), "Failed to fetch route", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


    }
//    private void showToast(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }
//    private void addImageToStyle(@NonNull Style style, @NonNull String imageId) {
//        Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baseline_location_on_24);
//        style.addImage(imageId, iconBitmap);
//    }
//
//    private void addMarkerAnnotation(@NonNull PointAnnotationManager pointAnnotationManager,
//                                     @NonNull String imageId) {
//        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
//                .withPoint(Point.fromLngLat(LONGITUDE, LATITUDE))
//                .withIconImage(imageId)
//                .withIconAnchor(IconAnchor.BOTTOM);
//        pointAnnotationManager.create(pointAnnotationOptions);
//    }

//    private void setupSearchView() {
//        SearchEngine searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
//                ApiType.GEOCODING,
//                new SearchEngineSettings()
//        );
//        searchBar.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchResultsView.searc;
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//    }


    @SuppressLint("Lifecycle")
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @SuppressLint("Lifecycle")
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @SuppressLint("Lifecycle")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        mapboxNavigation.unregisterRoutesObserver(routesObserver);
        mapboxNavigation.unregisterLocationObserver(locationObserver);
    }

    @SuppressLint("Lifecycle")
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}