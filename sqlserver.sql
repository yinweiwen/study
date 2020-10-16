SELECT request_session_id spid,OBJECT_NAME(resource_associated_entity_id)tableName
FROM  sys.dm_tran_locks
WHERE resource_type='OBJECT ' 

kill spid


sp_who active  --看看哪个引起的死锁, blk里面即阻塞的spid；  
dbcc inputbuffer(@blk) -- 可以查看是那个sql语句造成的死锁；  
sp_lock  --看看锁住了那个资源，objid即被锁住的资源id;  
select object_name(objid) --可得到受影响的表名；  
 
``` 查询表空间占用
CREATE TABLE #tablespaceinfo
    (
      nameinfo VARCHAR(500) ,
      rowsinfo BIGINT ,
      reserved VARCHAR(20) ,
      datainfo VARCHAR(20) ,
      index_size VARCHAR(20) ,
      unused VARCHAR(20)
    )

DECLARE @tablename VARCHAR(255);

DECLARE Info_cursor CURSOR
FOR
    SELECT  '[' + [name] + ']'
    FROM    sys.tables
    WHERE   type = 'U';

OPEN Info_cursor
FETCH NEXT FROM Info_cursor INTO @tablename

WHILE @@FETCH_STATUS = 0
    BEGIN
        INSERT  INTO #tablespaceinfo
                EXEC sp_spaceused @tablename
        FETCH NEXT FROM Info_cursor
    INTO @tablename
    END

CLOSE Info_cursor
DEALLOCATE Info_cursor

--创建临时表
CREATE TABLE [#tmptb]
    (
      TableName VARCHAR(50) ,
      DataInfo BIGINT ,
      RowsInfo BIGINT ,
      Spaceperrow  AS ( CASE RowsInfo
                         WHEN 0 THEN 0
                         ELSE CAST(DataInfo AS decimal(18,2))/CAST(RowsInfo AS decimal(18,2))
                       END ) PERSISTED
    )

--插入数据到临时表
INSERT  INTO [#tmptb]
        ( [TableName] ,
          [DataInfo] ,
          [RowsInfo]
        )
        SELECT  [nameinfo] ,
                CAST(REPLACE([datainfo], 'KB', '') AS BIGINT) AS 'datainfo' ,
                [rowsinfo]
        FROM    #tablespaceinfo
        ORDER BY CAST(REPLACE(reserved, 'KB', '') AS INT) DESC


--汇总记录
SELECT  [tbspinfo].* ,
        [tmptb].[Spaceperrow] AS '每行记录大概占用空间（KB）'
FROM    [#tablespaceinfo] AS tbspinfo ,
        [#tmptb] AS tmptb
WHERE   [tbspinfo].[nameinfo] = [tmptb].[TableName]
ORDER BY CAST(REPLACE([tbspinfo].[reserved], 'KB', '') AS INT) DESC

DROP TABLE [#tablespaceinfo]
DROP TABLE [#tmptb]
```

## 游标
```sql

DECLARE  @st int,@date datetime,@total int
set @date='2020-10-15'

delete from T_DATA_STATISTICS_DAY where DATE=@date

DECLARE cur CURSOR FOR select ID from T_DIM_STRUCTURE s where s.IsDelete=0
OPEN cur
FETCH NEXT FROM cur INTO @st
WHILE @@FETCH_STATUS = 0
begin
    set @total=0

    DECLARE @tb varchar(100)
    DECLARE cursor_name CURSOR FOR --定义游标
        select THEMES_TABLE_NAME from T_DIM_STRUCTURE_FACTOR sf,T_DIM_SAFETY_FACTOR_TYPE f where sf.SAFETY_FACTOR_TYPE_ID=f.SAFETY_FACTOR_TYPE_ID and sf.STRUCTURE_ID=@st and SAFETY_FACTOR_TYPE_PARENT_ID!=24
    OPEN cursor_name --打开游标
    FETCH NEXT FROM cursor_name INTO  @tb  --抓取下一行游标数据
    WHILE @@FETCH_STATUS = 0
        BEGIN
            if @tb!='T_THEMES_VIBRATION_ORIGINAL'
                begin
                    declare @s nvarchar(4000),@bbb int
                    set @s='select @cnt=count(*) from '+@tb+' d,T_DIM_SENSOR s where d.SENSOR_ID=s.SENSOR_ID and s.STRUCT_ID='+convert(varchar,@st)+' and d.ACQUISITION_DATETIME<''2020-10-16 20:00:00'''
                    PRINT 'exec: '+@s
                    exec sp_executesql @s,N'@cnt int output ',@bbb output
                    PRINT 'execute：'+@tb +' result:'+convert(varchar,@bbb)
                    if @bbb is not null
                        set @total=@total+@bbb
                end
            FETCH NEXT FROM cursor_name INTO @tb
        END
    CLOSE cursor_name --关闭游标
    DEALLOCATE cursor_name --释放游标

    PRINT 'total:'+convert(varchar,@total)

    insert into T_DATA_STATISTICS_DAY (STRUCT_ID, DATAS, ALARMS, DATE) values(@st,@total,0,@date)

    FETCH NEXT FROM cur INTO @st
end
CLOSE cur --关闭游标
DEALLOCATE cur --释放游标

GO
select * from T_DATA_STATISTICS_DAY;
select * from T_DATA_STATISTICS_DAY;
```