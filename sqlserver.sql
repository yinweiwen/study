SELECT request_session_id spid,OBJECT_NAME(resource_associated_entity_id)tableName
FROM  sys.dm_tran_locks
WHERE resource_type='OBJECT ' 

kill spid


sp_who active  --看看哪个引起的死锁, blk里面即阻塞的spid；  
dbcc inputbuffer(@blk) -- 可以查看是那个sql语句造成的死锁；  
sp_lock  --看看锁住了那个资源，objid即被锁住的资源id;  
select object_name(objid) --可得到受影响的表名；  