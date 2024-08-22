## Clickhouse-server无法启动问题排查



```sh
2024.07.10 09:18:37.259632 [ 412951 ] {} <Error> PushingPipelineExecutor: Code: 252. DB::Exception: Too many parts (300 with average size of 820.86 KiB). Merges are processing significantly slower than inserts. (TOO_MANY_PARTS), Stack trace (when copying this message, always include the lines below):
```
查看：https://clickhouse.com/docs/en/operations/settings/merge-tree-settings

parts_to_throw_insert
If the number of active parts in a single partition exceeds the parts_to_throw_insert value, INSERT is interrupted with the Too many parts (N). Merges are processing significantly slower than inserts exception.

Possible values:

Any positive integer.
Default value: 3000.

To achieve maximum performance of SELECT queries, it is necessary to minimize the number of parts processed, see Merge Tree.

Prior to 23.6 this setting was set to 300. You can set a higher different value, it will reduce the probability of the Too many parts error, but at the same time SELECT performance might degrade. Also in case of a merge issue (for example, due to insufficient disk space) you will notice it later than it could be with the original 300.

修改config中merge_tree段落中，将该值修改成100000（10万）。Two thousand years later。。。。

查看运行日志：
 tail -n1000 /home/clickhouse/log/clickhouse-server.log -f
 tail -n1000 /home/clickhouse/log/clickhouse-server.err.log

 一直重复日志：
```txt
2024.07.10 10:37:53.734664 [ 473284 ] {} <Debug> alarm.alarms (e9508ff5-a59b-4cfa-a38a-a7cdd5f9735a): Loading mutation: mutation_57738153.txt entry, commands size: 1
2024.07.10 10:37:53.750481 [ 473284 ] {} <Debug> alarm.alarms (e9508ff5-a59b-4cfa-a38a-a7cdd5f9735a): Loading mutation: mutation_57416696.txt entry, commands size: 1
2024.07.10 10:37:53.755618 [ 473284 ] {} <Debug> alarm.alarms (e9508ff5-a59b-4cfa-a38a-a7cdd5f9735a): Loading mutation: mutation_57140008.txt entry, commands size: 1

```

通过iostat -dx 1查看磁盘读写请求情况：
Device：设备名称（例如 sda）。
r/s：每秒读请求数。
rkB/s：每秒读的千字节数（kilobytes read per second）。
rrqm/s：每秒合并的读请求数（由于操作系统将多个读请求合并为一个读请求）。
%rrqm：合并读请求的百分比（rrqm/s 和 r/s 之间的百分比）。
r_await：读请求的平均等待时间（以毫秒为单位）。
rareq-sz：平均读请求大小（读的千字节数除以读请求数）。
w/s：每秒写请求数。
wkB/s：每秒写的千字节数（kilobytes written per second）。
wrqm/s：每秒合并的写请求数（由于操作系统将多个写请求合并为一个写请求）。
%wrqm：合并写请求的百分比（wrqm/s 和 w/s 之间的百分比）。
w_await：写请求的平均等待时间（以毫秒为单位）。
wareq-sz：平均写请求大小（写的千字节数除以写请求数）。
d/s：每秒丢弃的请求数。
dkB/s：每秒丢弃的千字节数。
drqm/s：每秒合并的丢弃请求数。
%drqm：合并丢弃请求的百分比。
d_await：丢弃请求的平均等待时间（以毫秒为单位）。
dareq-sz：平均丢弃请求大小（丢弃的千字节数除以丢弃请求数）。
f/s：每秒刷新请求数。
f_await：刷新请求的平均等待时间（以毫秒为单位）。
aqu-sz：活动队列的平均大小。
%util：设备的利用率百分比（表示设备在测量间隔内忙于处理 I/O 请求的时间比例）。