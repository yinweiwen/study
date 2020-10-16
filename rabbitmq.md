# RABBIT-MQ
## install
https://www.rabbitmq.com/install-debian.html#apt

## start
```
service rabbitmq-server start

# 启用managerment
rabbitmq-plugins enable rabbitmq_management

http://localhost:15672   guest/guest

```

## client
### C#
[RabbitMQ.Client](https://www.rabbitmq.com/dotnet-api-guide.html#dependencies)


Sender
```C#

        public void Push(string exchange, string queue, string msg)
        {
            var factory = new ConnectionFactory()
            {
                HostName = _config.ip,
                Port = _config.port,
                UserName = _config.user,
                Password = _config.pwds,
                VirtualHost = _config.VirtualHost
            };
            using (var connection = factory.CreateConnection())
            using (var channel = connection.CreateModel())
            {
                channel.QueueDeclare(queue: queue,
                                     durable: false,
                                     exclusive: false,
                                     autoDelete: false,
                                     arguments: null);

                var body = Encoding.UTF8.GetBytes(msg);

                channel.BasicPublish(exchange: exchange,
                                     routingKey: queue,
                                     basicProperties: null,
                                     body: body);
                _log.InfoFormat("Sent {0}", msg);
            }
        }
```

Receive
```C#

        static void Main(string[] args)
        {
            var topics = new[] { "tailingInfo", "equipInfo.05", "dataInfo.05", "warningInfo.05" };
            var factory = new ConnectionFactory() { HostName = "localhost", Port = 5672, UserName = "guest", Password = "guest" };
            using (var connection = factory.CreateConnection())
            {
                using (var channel = connection.CreateModel())
                {
                    foreach (var topic in topics)
                    {
                        channel.QueueDeclare(queue: topic,
                                             durable: false,
                                             exclusive: false,
                                             autoDelete: false,
                                             arguments: null);
                    }


                    var consumer = new EventingBasicConsumer(channel);
                    consumer.Received += (model, ea) =>
                    {
                        var body = ea.Body.ToArray();
                        var message = Encoding.UTF8.GetString(body);
                        Console.WriteLine(" [x] Received {0}", message);
                    };

                    foreach (var topic in topics)
                    {
                        channel.BasicConsume(queue: topic,
                                             noAck: true,
                                             consumer: consumer);
                    }

                    Console.WriteLine(" Press [enter] to exit.");
                    Console.ReadLine();
                }
            }
        }
```

## Exchange route queue
![rabbit](img/rabbit1.png)