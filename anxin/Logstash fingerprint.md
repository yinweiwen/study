## Logstash fingerprint

> 需求：需要在Logstash ES数据插入的时候，指定document的_ID属性。



Ruby

```ruby
def hi(name)
    puts "Hello world! #{name}!"
end

hi("ww")

def hi(name="world")
    puts "hello #{name.capitalize}" 
end

# class
class Greeter
    def initialize(name="world")
        @name=name
    end
    def say_hi
        puts "Hi,#{@name}!"
    end
end

greeter=Greeter.new("pat")
greeter.say_hi


#Using attr_accessor defined two new methods for us, name to get the value, and name= to set it.
class Greeter
  attr_accessor :name
end
greeter.name="Betty"
greeter.name


```

for.rb

```ruby
#!/usr/bin/env ruby

class MegaGreeter
  attr_accessor :names

  # Create the object
  def initialize(names = "World")
    @names = names
  end

  # Say hi to everybody
  def say_hi
    if @names.nil?
      puts "..."
    elsif @names.respond_to?("each")
      # @names is a list of some kind, iterate!
      @names.each do |name|
        puts "Hello #{name}!"
      end
    else
      puts "Hello #{@names}!"
    end
  end

  # Say bye to everybody
  def say_bye
    if @names.nil?
      puts "..."
    elsif @names.respond_to?("join")
      # Join the list elements with commas
      puts "Goodbye #{@names.join(", ")}.  Come back soon!"
    else
      puts "Goodbye #{@names}.  Come back soon!"
    end
  end
end


if __FILE__ == $0
  mg = MegaGreeter.new
  mg.say_hi
  mg.say_bye

  # Change name to be "Zeke"
  mg.names = "Zeke"
  mg.say_hi
  mg.say_bye

  # Change the name to an array of names
  mg.names = ["Albert", "Brenda", "Charles",
              "Dave", "Engelbert"]
  mg.say_hi
  mg.say_bye

  # Change to nil
  mg.names = nil
  mg.say_hi
  mg.say_bye
end
```



没有安装windows下的交互模式（其他系统irb进入）

```shell
C:\Users\yww08\Desktop>ruby for.rb
```



## Logstash

生成plugin模板

```shell
E:\WorkSpace\logstash-8.5.1>bin\logstash-plugin.bat generate --type filter --name fingerprint --path ./out
"Using bundled JDK: E:\WorkSpace\logstash-8.5.1\jdk\bin\java.exe"
 Creating ./out/logstash-filter-fingerprint
         create logstash-filter-fingerprint/CHANGELOG.md
         create logstash-filter-fingerprint/CONTRIBUTORS
         create logstash-filter-fingerprint/DEVELOPER.md
         create logstash-filter-fingerprint/docs/index.asciidoc
         create logstash-filter-fingerprint/Gemfile
         create logstash-filter-fingerprint/lib/logstash/filters/fingerprint.rb
         create logstash-filter-fingerprint/LICENSE
         create logstash-filter-fingerprint/logstash-filter-fingerprint.gemspec
         create logstash-filter-fingerprint/Rakefile
         create logstash-filter-fingerprint/README.md
         create logstash-filter-fingerprint/spec/filters/fingerprint_spec.rb
         create logstash-filter-fingerprint/spec/spec_helper.rb
```



```json
input {
  kafka{
    bootstrap_servers => "${ANXIN_KAFKA_BROKERS:localhost:9092}"
	topics => ["anxinyun_theme"]
	group_id => "anxin-logstash-themes-consumer"
	consumer_threads => 5
	codec => json {
	    charset => "UTF-8"
	}
  }
}

filter {
    mutate {
      rename => {"acqTime" => "collect_time"}
      rename => {"@timestamp" => "create_time"}
      add_field => {
        "sensor" => "%{[station][id]}"
      }
      add_field => {
        "sensor_name" => "%{[station][name]}"
      }
      add_field => {
        "factor" => "%{[station][factor][id]}"
      }
      add_field => {
        "factor_name" => "%{[station][factor][name]}"
      }
      add_field => {
        "structure" => "%{[station][structure][id]}"
      }
      add_field => {
        "factor_proto_code" => "%{[station][factor][protoCode]}"
      }
      add_field => {
        "factor_proto_name" => "%{[station][factor][protoName]}"
      }
      remove_field => ["station", "@version", "@timestamp", "taskId", "deviceDatas", "state","dataEmpty","rawAgg"]
    }

	fingerprint {
		method => "UUIDIOTA"
		source => ["sensor", "collect_time"]
		target => "[@metadata][id]"
	}
}

output {
  stdout {  
        codec => json_lines  
    }
  elasticsearch {
      hosts => ["${ANXIN_ELASTICSEARCH_NODES:localhost:9200}"]
      index => "${ANXIN_ELASTICSEARCH_INDEX:anxinyun_themes}"
      flush_size => 100
      sniffing => true
	  document_id =>"%{[@metadata][id]}"
  }
}
```



替换fingerprint.rb文件

```shell
logstash-8.5.1\vendor\bundle\jruby\2.6.0\gems\logstash-filter-fingerprint-3.4.1\lib\logstash\filters
```



Running On Windows:

```shell
set ANXIN_KAFKA_BROKERS=10.8.30.72:29092
set ANXIN_ELASTICSEARCH_NODES=10.8.30.60:9200
.\bin\logstash.bat -f .\config\logstash.conf
```

