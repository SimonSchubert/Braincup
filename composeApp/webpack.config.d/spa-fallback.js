// Serve index.html for deep-link paths (e.g. /MiniChess) so the WASM app can boot
// and parse the pathname. Required for path-based bindToBrowserNavigation URLs;
// see https://kotlinlang.org/docs/multiplatform/compose-navigation-routing.html
config.devServer = config.devServer || {};
config.devServer.historyApiFallback = true;