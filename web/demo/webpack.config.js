var path = require('path');
var webpack = require('webpack');
var BundleAnalyzerPlugin = require('webpack-bundle-analyzer').BundleAnalyzerPlugin;

require('babel-polyfill');

const PATHS = {
    app: path.join(__dirname, 'client/src'),
    build: path.join(__dirname, 'client/build')
};

module.exports = {
    devtool: 'source-map',
    entry: {
        app: ["babel-polyfill", PATHS.app]
    },
    output: {
        publicPath: 'http://localhost:3001/client/build',
        path: PATHS.build,
        filename: '[name].js'
    },
    resolve: {
        modules: [path.resolve(__dirname, 'client/src'), path.resolve(__dirname, 'node_modules')],
        extensions: ['.js', '.jsx'],
        alias :{ moment$: 'moment/moment.js' }
    },
    plugins: [
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('development')
        }),
        new webpack.HotModuleReplacementPlugin(),
        new BundleAnalyzerPlugin(),
        new webpack.optimize.CommonsChunkPlugin({
            name: "vendor", minChunks: ({ resource }) => (
                resource &&
                resource.indexOf('node_modules') >= 0 &&
                resource.match(/\.js$/)
            ),
        })
    ],
    module: {
        rules: [{
            test: /\.css$/,
            use: ['style-loader', 'css-loader?modules&camelCase']
        },
        {
            test: /\.less$/,
            use: ['style-loader', 'css-loader', 'less-loader']
        },
        {
            test: /\.jsx?$/,
            use: 'babel-loader',
            include: PATHS.app
        }]
    }
};