#!/bin/sh

if [[ $1 = "stop" ]] || [[ $1 = "start" ]] || [[ $1 = "restart" ]]; then
	
	#应用名称
	appname='blog-2.0.0'
	#目录
	dirname='/www/server/shainesfiles/app/blog/'
	#jar文件路径
	jarname='blog-2.0.0.jar'
	#jvm参数
	javavms='-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m'
	#等待停止时长-秒
	stopwts=20
	
	#检查日志目录
	test -d ${dirname}/logs/ || mkdir -p ${dirname}/logs/
	
	## 停止
	if [[ $1 = "stop" ]] || [[ $1 = "restart" ]] ; then
		if test $( pgrep -f ${jarname} | wc -l ) -eq 0 ; then
			## 未启动
			if [[ $1 = "stop" ]] ; then
				echo "${appname} is already stoped."
			fi
		else
			## 停止
			echo "${appname} is stopping ..."
			kill -15 $( pgrep -f ${jarname} )
			
			sleep 4
			wts=0
			while true
			do
				if test $( pgrep -f ${jarname} | wc -l ) -eq 0 ; then
				  echo "${appname} is stopped"
				  break
				else
					sleep 1
					let "wts++"
					echo "        ... waited ${wts}s "
					if (( $wts >= stopwts )); then
						## 杀进程
						echo "wait ${wts} times, kill 9 ${appname}."
						sudo kill -9 $( pgrep -f ${jarname} )
						break
					fi
				fi
			done
				
		fi
	fi
	## 启动
	if [[ $1 = "start" ]] || [[ $1 = "restart" ]] ; then
		if test $( pgrep -f ${jarname} | wc -l ) -eq 0 ; then
			## 删除日志
			if [[ $2 = "c" ]] ; then
				rm -rf ${dirname}logs/*
			fi
			## 启动
			## nohup java ${javavms} -jar ${dirname}${jarname} > ${dirname}logs/debug.log 2>&1 &
			nohup java ${javavms} -jar ${dirname}${jarname} --spring.profiles.active=prod > /dev/null 2>&1 &
			echo "${appname} is started. [  tail -f ${dirname}logs/debug.log  ] "
		else
			echo "${appname} is already started."
		fi
	fi
else
	echo 'requires parameter [start|stop|restart] [c]? '
fi

