var path = require('path');
var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');

const PATHS = {
    app: path.join(__dirname, 'client/src'),
    build: path.join(__dirname, 'client/build')
};

module.exports = {
    entry: {
        app: ["babel-polyfill", PATHS.app]
    },
    output: {
        path: PATHS.build,
        publicPath: '/build',
        filename: '[name].[hash:5].js'
    },
    resolve: {
        modules: [path.resolve(__dirname, 'client/src'), path.resolve(__dirname, 'node_modules')],
        extensions: ['.js', '.jsx'],
        alias :{ moment$: 'moment/moment.js' }
    },
    plugins: [
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('production')
        }),
        new webpack.optimize.CommonsChunkPlugin({
            name: "vendor", minChunks: ({ resource }) => (
                resource &&
                resource.indexOf('node_modules') >= 0 &&
                resource.match(/\.js$/)
            ),
        }),
        new HtmlWebpackPlugin({
            filename: '../index.html',
            template: './client/index.ejs'
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
