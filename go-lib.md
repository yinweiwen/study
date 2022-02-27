## Cobra

https://github.com/spf13/cobra

Cobra 用于创建强大的现代 CLI 应用程序的库。

支持子命令，posix 规范的 `Flag`，嵌套的子命令，支持全局、局部和级联的选项，支持 bash 自动补全，便捷的参数校验。





## Rate

golang.org/x/time/rate

Golang标准库限流器，是基于 Token Bucket(令牌桶) 实现的

```go
limiter:=NewLimiter(r Limit, b int)
limiter.WaitN(ctx context.Context, n int)
```



