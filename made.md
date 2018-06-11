MARKDOWN DSL
=====
<http://xianbai.me/learn-md/article/syntax/paragraphs-and-line-breaks.html>
<br>
## h2

> quote
>> secondary quote

>1(two space here to break line.)  
2'nd line

* mark
+ mark
- mark

1. serial1
    + chapt 1.1
2. serial2
    + chapt 1.2
        + 1.2.1
        + 1.2.2

insert code block:(use tab)

    <html>
        <title>Markdown</title>
    </html>

insert code in line: `<some code here>`

seperate line here: 
****
----
____

ahref link: [Google](http://www.google.com/) [map.png](../../xx.png)<br>
[Google](http://www.google.com/ "SOME-Title here")<br>
ref link type: [NewGoogle][link]<br>
auto link type: <www.baidu.com>

[link]: http://www.google.com/ "Google"

Show some picture here:![GitHub][github]

[github]: https://avatars2.githubusercontent.com/u/3265208?v=3&s=100 "GitHub,Social Coding"

Emphasize text here: <br>
*enphasize*  _this is_<br>
**strong** __this is__

~~DELETE THIS~~<br>

Code block:
```js

let findById = async function (id, config) {
    const defaultConfig = require('../../../config').elasticsearch;
    config = config || defaultConfig;
    try {
        let client = await esClient;
        if (client && id) {
            let res = await client.get({
                index: config.index,
                type: config.type,
                id: id
            });
            let rslt = res && res.found ? res._source : null;
            return rslt;
        } else {
            throw 'es client or id may be null';
        }
    } catch (err) {
        logger.error('[ALARM]-[findById] error:', err);
    }
};
```

**TABLE** now:<br>
name | sex
----|----
Mike|male
Lisa|female

table align use: :---,:--:,---:<br>
table cell can use line sytax: __strong cell__

