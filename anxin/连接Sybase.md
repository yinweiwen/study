连接Sybase



```sql
# Install pre-requesite packages
sudo apt-get install unixodbc unixodbc-dev freetds-dev freetds-bin tdsodbc
```

Point odbcinst.ini to the driver in /etc/odbcinst.ini:

```sql
[FreeTDS]
Description = v0.91 with protocol v5.0
Driver = /usr/lib/x86_64-linux-gnu/odbc/libtdsodbc.so
```

Create your DSNs in odbc.ini:

```sql
[sybase]
Driver = FreeTDS
Server = 19.15.78.123
Port = 7220
TDS_Version = 5.0
Database = gjj
```

...and your DSNs in `freetds.conf`:

```sql
[global]
    # TDS protocol version, use:
    # 7.3 for SQL Server 2008 or greater (tested through 2014)
    # 7.2 for SQL Server 2005
    # 7.1 for SQL Server 2000
    # 7.0 for SQL Server 7
    tds version = 7.2
    port = 1433

    # Whether to write a TDSDUMP file for diagnostic purposes
    # (setting this to /tmp is insecure on a multi-user system)
;   dump file = /tmp/freetds.log
;   debug flags = 0xffff

    # Command and connection timeouts
;   timeout = 10
;   connect timeout = 10

    # If you get out-of-memory errors, it may mean that your client
    # is trying to allocate a huge buffer for a TEXT field.  
    # Try setting 'text size' to a more reasonable limit 
    text size = 64512

# A typical Microsoft server
[dbserverdsn]
    host = dbserver.domain.com
    port = 1433
    tds version = 7.2
    
[sybase]
        host=10.8.30.32
        port=1433
        tds version=5.0

```

After completing this, you can test your connection by attempting to connect with tsql (to test the FreeTDS layer) and isql (for the unixODBC through FreeTDS stack).

```
tsql -S mssql -U sa -P 123
```

```sh
**********************************************
* unixODBC - isql                            *
**********************************************
* Syntax                                     *
*                                            *
*      isql DSN [UID [PWD]] [options]        *
*                                            *
* Options                                    *
*                                            *
* -b         batch.(no prompting etc)        *
* -dx        delimit columns with x          *
* -x0xXX     delimit columns with XX, where  *
*            x is in hex, ie 0x09 is tab     *
* -w         wrap results in an HTML table  结果按HTML表格展示 *
* -c         column names on first row.      *
*            (only used when -d)             *
* -mn        limit column display width to n *
* -v         verbose.                        *
* -lx        set locale to x                 *
* -q         wrap char fields in dquotes     *
* -3         Use ODBC 3 calls                *
* -n         Use new line processing         *
* -e         Use SQLExecDirect not Prepare   *
* -k         Use SQLDriverConnect            *
* -L         Length of col display (def:300) *
* --version  version                         *
*                                            *
* Commands                                   *
*                                            *
* help - list tables                         *
* help table - list columns in table         *
* help help - list all help options          *
*                                            *
* Examples                                   *
*                                            *
*      isql WebDB MyID MyPWD -w < My.sql     *
*                                            *
*      Each line in My.sql must contain      *
*      exactly 1 SQL command except for the  *
*      last line which must be blank (unless *
*      -n option specified).                 *
*                                            *
* Please visit;                              *
*                                            *
*      http://www.unixodbc.org               *
*      nick@lurcher.org                      *
*      pharvey@codebydesign.com              *
**********************************************
```

执行isql

```sh
isql DNS uid pwd
```



查询所有表

```sql
select ob.name,st.rowcnt 
from sysobjects ob, systabstats st 
where ob.type="U"  
and st.id=ob.id 
order by ob.name;

-- 或者
SELECT b.table_name "Object Name",
 c.user_name "Owner",
 b.object_id "ID",
 a.object_type "Type",
 a.status "Status"
 FROM ( SYSOBJECT a JOIN SYSTAB b
 ON a.object_id = b.object_id )
 JOIN SYSUSER c
WHERE c.user_name = 'SYS'
 OR c.user_name = 'dbo'
ORDER BY c.user_name, b.table_name;
```



参考

[1] https://stackoverflow.com/questions/33341510/how-to-install-freetds-in-linux

[2] 