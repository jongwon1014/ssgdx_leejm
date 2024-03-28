#!/bin/sh
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:"/engn001/cubeone/lib"

# DB
export MDS_MASTER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-dev-opmdsdbd.cluster-c92dc3uwdt0o.ap-northeast-2.rds.amazonaws.com:3306/MDSDEV
export MDS_USER=arn:aws:secretsmanager:ap-northeast-2:200089571166:secret:dept-sm-an2-op-dev-opmds-oRpHO4
export MDS_READER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-dev-opmdsdbd.cluster-ro-c92dc3uwdt0o.ap-northeast-2.rds.amazonaws.com:3306/MDSDEV
export SLC_USER=arn:aws:secretsmanager:ap-northeast-2:200089571166:secret:dept-sm-an2-op-dev-opslc-ihkCwL
export SLC_MASTER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-dev-opslcdbd.cluster-c92dc3uwdt0o.ap-northeast-2.rds.amazonaws.com:3306/SLCDEV
export SLC_READER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-dev-opslcdbd.cluster-ro-c92dc3uwdt0o.ap-northeast-2.rds.amazonaws.com:3306/SLCDEV
export DMS_USER=arn:aws:secretsmanager:ap-northeast-2:200089571166:secret:dept-sm-an2-op-dev-opdms-dcMl3N
export DMS_MASTER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-dev-opdmsdbd.cluster-c92dc3uwdt0o.ap-northeast-2.rds.amazonaws.com:3306/DMSDEV
export DMS_READER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-dev-opdmsdbd.cluster-ro-c92dc3uwdt0o.ap-northeast-2.rds.amazonaws.com:3306/DMSDEV
export SECRETS_DRIVER_CLASS=com.amazonaws.secretsmanager.sql.AWSSecretsManagerMySQLDriver
export AWS_DRIVER_CLASS=software.aws.rds.jdbc.mysql.Driver

# Redis
export REDIS_URL=dept-ecc-an2-cm-dev-redis.lfawe4.clustercfg.apn2.cache.amazonaws.com:6379
# Secrets Manager
export SECRETS_MANAGER_OTHER_KEY=arn:aws:secretsmanager:ap-northeast-2:200089571166:secret:dept-sm-an2-op-dev-other-ea5n5v
# AWS S3
export DEFAULT_S3_BUCKET=dept-s3-an2-cm-dev-test
# MS API (DRM)
export MSAIP_SYSTEM_ID=92048C08-8BC7-4168-BE9F-973E47F26771
export MSAIP_SHARE_PATH=\\\\100.67.13.61\\Shared
# AWS SNS (Push)
export PUSH_SNS_TOPIC=ssgdx-push-dev
export PUSH_PLATFORM_APPL=ssgdx-push-list-dev
# AWS SQS
export SQS_THREADPOOL_CORE_SIZE=10
export SQS_THREADPOOL_MAX_SIZE=10
export SQS_MAX_NUMBER_OF_MESSAGE=10
# DataDog
export DATADOG_URL=https://api.datadoghq.com
export DATADOG_API_KEY=ENC\(AulJDwUl0zjRBBYHQg6wLUnol8fdUEzPyNrb8GpCnx8upjAseyv3LPf1+fDuf0G0LU3gTsfdPyow+CVddYVgjw==\)
export DATADOG_APPL_KEY=ENC\(fkuj4toOdX/vB446l2O9I0Ie4wV2DDMeVk0+vGWttKnjmt8D6lgHT29BrStvDKUywDsIG3gItRTrIjKf4/cFZg==\)
# JJobs (Ondemand Batch)
export JJOBS_URL=http://100.67.13.60:8080/jjob-manager
export JJOBS_TOKEN=ENC\(S+AZVnhcpziDzrcbuWmxA9nl19A6gL7d7Yb4dF40IN/1lr25toS4zPsJ7v/p+soq\)

# export MDS_MASTER_URL MDS_USER SECRETS_DRIVER_CLASS MDS_READER_URL REDIS_URL SLC_USER SLC_MASTER_URL SLC_READER_URL DMS_USER DMS_MASTER_URL DMS_READER_URL AWS_DRIVER_CLASS


nohup java -javaagent:/sorc001/dd-java-agent.jar \
  -Ddd.profiling.enabled=true \
  -Ddd.env=dev \
  -Ddd.service=dev-op-ldi  \
  -Ddd.trace.debug=false \
  -Ddd.logs.injection=true \
  -Ddd.trace.sample.rate=1 \
  -Ddd.service.mapping=mysql:dms-mysql \
  -XX:FlightRecorderOptions=stackdepth=256 \
  -Ddatadog.slf4j.simpleLogger.logFile=/logs001/DataDog3.log \
  -jar -Dspring.profiles.active=dev /sorc001/ssgdx-ldi.jar  > /dev/null 2>&1 &

exitStatus=$?

echo $exitStatus
