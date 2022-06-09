# Skeleton2022
Template for Android app test assignments - the year 2022 edition


The app's functionality is pretty simple: two screens; one screen is a list of products in a shop, and the other is a detailed product screen.
The data are taken from a single static JSON file requested by URL. The implementation contains a cache for reducing network traffic.

Source code is organised accordingly to the clean architecture approach. It contains a domain layer (business logic), a data layer (everything platform-specific), and a presentation layer (UI). The domain and data layers know nothing about other layers; the presentation layer is aware of the domain layer. Dependency inversion is used to interact between the data and domain layers, which means the domain layer contains interfaces that must be implemented in the data layer. Thankfully, the presentation layer can work directly with the domain layer.

All the objects are instantiated by the system (fragments, view models) or in a Dagger component. For simplicity, the app contains just one Dagger component in the app scope - instances are effectively singletons. The Dagger component "lives" in an Application class and can be accessed via an extension method.

The interaction between layers is implemented using coroutines, and multithread scheduling is also coroutine based. ViewModels use StateFlow for fragments to acquire states.

ViewModels are provided using JetPack ViewModels Framework with a custom factory. The Dagger component provides the custom factory.
Every ViewModel uses an MVI pattern: it contains StateFlow that emits states. Each state contains all the information to draw the screen. All the states are descendants of a sealed class State. No reducers are used here as all the states are fully independent in such a simple app. Our MVI implementation doesn't have a state machine.

The JetPack Navigation framework does the navigation. Yes, it's an overengineering, but we better use it from the very beginning to scale up the app later.

Views are classic layouts within fragments plus a single main activity. The loading and the error screens are fully implemented; the error screens have refresh buttons; the list screen has an integrated SwipeRefreshLayout.

The network requests are implemented via Retrofit with JSON parsing made by GSON. All the requests are logged via HttpLoggingInterceptor by Square.

-------------

Branch *compose* contains composable functions instead of classical views and fragments. The navigation is made via Compose Navigation Framework. Actually, each screen is a set of two things: a composable function and a ViewModel. ViewModels are injected into their respectful composable functions.
