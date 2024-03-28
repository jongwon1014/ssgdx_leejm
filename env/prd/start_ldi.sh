#!/bin/sh
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:"/engn001/cubeone/lib"
# DB
export MDS_USER=dept-sm-an2-op-prd-opmds
export MDS_MASTER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-prd-opmdsdbp.cluster-cnepgvjrcnpn.ap-northeast-2.rds.amazonaws.com:3306/MDSPRD
export MDS_READER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-prd-opmdsdbp.cluster-ro-cnepgvjrcnpn.ap-northeast-2.rds.amazonaws.com:3306/MDSPRD
export SLC_USER=dept-sm-an2-op-prd-opslc
export SLC_MASTER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-prd-opslcdbp.cluster-cnepgvjrcnpn.ap-northeast-2.rds.amazonaws.com:3306/SLCPRD
export SLC_READER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-prd-opslcdbp.cluster-ro-cnepgvjrcnpn.ap-northeast-2.rds.amazonaws.com:3306/SLCPRD
export DMS_USER=dept-sm-an2-op-prd-opdms
export DMS_MASTER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-prd-opdmsdbp.cluster-cnepgvjrcnpn.ap-northeast-2.rds.amazonaws.com:3306/DMSPRD
export DMS_READER_URL=jdbc-secretsmanager:log4jdbc:mysql:aws://dept-rds-an2-op-prd-opdmsdbp.cluster-ro-cnepgvjrcnpn.ap-northeast-2.rds.amazonaws.com:3306/DMSPRD
export SECRETS_DRIVER_CLASS=com.amazonaws.secretsmanager.sql.AWSSecretsManagerMySQLDriver
export AWS_DRIVER_CLASS=software.aws.rds.jdbc.mysql.Driver
export REDIS_URL=dept-ecc-an2-cm-prd-redis.wojkal.clustercfg.apn2.cache.amazonaws.com:6379

# Secrets Manager
export SECRETS_MANAGER_OTHER_KEY="dept-sm-an2-op-prd-other"
# AWS S3
export DEFAULT_S3_BUCKET=dept-s3-an2-cm-prd-app
# MS AIP (DRM)

# AWS SNS (Push)
export PUSH_SNS_TOPIC=ssgdx-push-prd
export PUSH_PLATFORM_APPL=ssgdx-push-list-prd
# AWS SQS 
export SQS_THREADPOOL_CORE_SIZE=10
export SQS_THREADPOOL_MAX_SIZE=10
export SQS_MAX_NUMBER_OF_MESSAGE=10
# DataDog (APM)
export DATADOG_URL=https://api.datadoghq.com
export DATADOG_API_KEY=ENC\(AulJDwUl0zjRBBYHQg6wLUnol8fdUEzPyNrb8GpCnx8upjAseyv3LPf1+fDuf0G0LU3gTsfdPyow+CVddYVgjw==\)
export DATADOG_APPL_KEY=ENC\(fkuj4toOdX/vB446l2O9I0Ie4wV2DDMeVk0+vGWttKnjmt8D6lgHT29BrStvDKUywDsIG3gItRTrIjKf4/cFZg==\)
# JJobs (Ondemand Batch)
export JJOBS_URL=http://internal-dept-elb-an2-op-prd-deptopbtsapp-128933556.ap-northeast-2.elb.amazonaws.com:8080/jjob-manager
export JJOBS_TOKEN=ENC\(BWn/VPrkDzEYhVHGdJ9FfrN8FRoO+jOS0JDBr2EwhvBC41O1xmz54WH6XrXLgxMX\)
export PROFILE=prd

nohup java -javaagent:/sorc001/dd-java-agent.jar \
  -Ddd.profiling.enabled=true \
  -Ddd.env=prd \
  -Ddd.service=prd-op-ldi  \
  -Ddd.trace.debug=false \
  -Ddd.logs.injection=true \
  -Ddd.trace.sample.rate=1 \
  -Ddd.service.mapping=mysql:dms-mysql \
  -XX:FlightRecorderOptions=stackdepth=256 \
  -Ddatadog.slf4j.simpleLogger.logFile=/logs001/DataDog3.log \
  -jar -Dspring.profiles.active=prd /sorc001/ssgdx-ldi.jar  > /dev/null 2>&1 &

exitStatus=$?

echo $exitStatus
