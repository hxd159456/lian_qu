pipeline {
    agent any
    tools{
        maven "default maven"
        jdk "java_8"
    }

    stages {
        stage('Pull Code') {
            steps {
                echo 'Pull Code'
                git credentialsId: 'a98a09af-6901-4e7b-b61a-e4a4424f6a9b', url: 'https://gitee.com/hxd_a/art_dev.git'
            }
        }
        stage('Build Project') {
            steps{
                echo 'Maven Build'
              //使用mvn构建，
                 //使用mvn构建，mvn clean install -pl <module-name>  P_A_A_B  这个是 业务侧代码 需要 同步更新部署
                sh 'mvn clean package -Dmaven.test.skip=true  -Dcheckstyle.skip=true -Dpmd.skip=true -Dfindbugs.skip=true'
                sh 'echo  打包成功 '

            }
        }
        stage("Build Docker Image And Deploy Local"){
            steps{
                echo 'Build Docker Image and Deploy Local'

                sh 'docker stop art-market-author'
                sh 'docker stop art-market-chain'
                sh 'docker stop art-market-gateway'
                sh 'docker stop art-market-notice'
                sh 'docker stop art-market-order'
                sh 'docker stop art-market-seckill'
                sh 'docker stop art-market-sms'
                sh 'docker stop art-market-trade'
                sh 'docker stop art-market-user'

                echo 'sleep 5'
                echo '全部停止！'

                sh 'docker rm art-market-author'
                sh 'docker rm art-market-chain'
                sh 'docker rm art-market-gateway'
                sh 'docker rm art-market-notice'
                sh 'docker rm art-market-order'
                sh 'docker rm art-market-seckill'
                sh 'docker rm art-market-sms'
                sh 'docker rm art-market-trade'
                sh 'docker rm art-market-user'
                sh 'sleep 5'
                echo '移除容器！'

                sh 'docker rmi art-market-author:1.0'
                sh 'docker rmi art-market-chain:1.0'
                sh 'docker rmi art-market-gateway:1.0'
                sh 'docker rmi art-market-notice:1.0'
                sh 'docker rmi art-market-order:1.0'
                sh 'docker rmi art-market-seckill:1.0'
                sh 'docker rmi art-market-sms:1.0'
                sh 'docker rmi art-market-trade:1.0'
                sh 'docker rmi art-market-user:1.0'

                echo '移除镜像！'

                sh 'docker build -t art-market-author:1.0 ./art-market-author'

                echo '作者模块镜像打包成功'
                sh 'docker build -t art-market-chain:1.0 ./art-market-chain'
                echo '区块链模块镜像打包成功'
                sh 'docker build -t art-market-gateway:1.0 ./art-market-gateway'
                echo '网关模块镜像打包成功'
                sh 'docker build -t art-market-notice:1.0 ./art-market-notice'
                echo '公告模块镜像打包成功'
                sh 'docker build -t art-market-order:1.0 ./art-market-order'
                echo '订单模块镜像打包成功'
                sh 'docker build -t art-market-seckill:1.0 ./art-market-seckill'
                echo '秒杀模块镜像打包成功'
                sh 'docker build -t art-market-sms:1.0 ./art-market-sms'
                echo '短信模块镜像打包成功'
                sh 'docker build -t art-market-trade:1.0 ./art-market-trade'
                echo '交易模块镜像打包成功'
                sh 'docker build -t art-market-user:1.0 ./art-market-user'
                echo '用户模块镜像打包成功'

                sh 'docker run -d -p 18881:8881 --name  art-market-author art-market-author:1.0'
                sh 'docker run -d -p 18888:8888 --name  art-market-chain art-market-chain:1.0'
                sh 'docker run -d -p 20088:88 --name  art-market-gateway art-market-gateway:1.0'
                sh 'docker run -d -p 18890:8890 --name  art-market-notice art-market-notice:1.0'
                sh 'docker run -d -p 19010:9010 --name  art-market-order art-market-order:1.0'
                sh 'docker run -d -p 19020:9020 --name  art-market-seckill art-market-seckill:1.0'
                sh 'docker run -d -p 18891:8891 --name  art-market-sms art-market-sms:1.0'
                sh 'docker run -d -p 19000:9000 --name  art-market-trade art-market-trade:1.0'
                sh 'docker run -d -p 18889:8889 --name  art-market-user art-market-user:1.0'

                echo '部署完成！'
            }
        }
        /**
        stage("Deploy Local"){
            steps{
                sh 'docker stop art-market-author'
                sh 'docker stop art-market-chain'
                sh 'docker stop art-market-gateway'
                sh 'docker stop art-market-notice'
                sh 'docker stop art-market-order'
                sh 'docker stop art-market-seckill'
                sh 'docker stop art-market-sms'
                sh 'docker stop art-market-trade'
                sh 'docker stop art-market-user'

                sh 'sleep 5'
                sh 'docker rm art-market-author'
                sh 'docker rm art-market-chain'
                sh 'docker rm art-market-gateway'
                sh 'docker rm art-market-notice'
                sh 'docker rm art-market-order'
                sh 'docker rm art-market-seckill'
                sh 'docker rm art-market-sms'
                sh 'docker rm art-market-trade'
                sh 'docker rm art-market-user'
                sh 'sleep 5'

                sh 'docker run -d -p 18881:8881 --name  art-market-author art-market-author:1.0'
                sh 'docker run -d -p 18888:8888 --name  art-market-chain art-market-chain:1.0'
                sh 'docker run -d -p 20088:88 --name  art-market-gateway art-market-gateway:1.0'
                sh 'docker run -d -p 18890:8890 --name  art-market-notice art-market-notice:1.0'
                sh 'docker run -d -p 19010:9010 --name  art-market-order art-market-order:1.0'
                sh 'docker run -d -p 19020:9020 --name  art-market-seckill art-market-seckill:1.0'
                sh 'docker run -d -p 18891:8891 --name  art-market-sms art-market-sms:1.0'
                sh 'docker run -d -p 19000:9000 --name  art-market-trade art-market-trade:1.0'
                sh 'docker run -d -p 18889:8889 --name  art-market-user art-market-user:1.0'

                echo '部署完成！'
            }
        }*/
        /**
        stage("Push Docker Image"){
            steps{
                echo 'Push Docker Image'

                withCredentials([usernamePassword(credentialsId: '26abb2c2-ff39-4521-98a1-454f908608d3', passwordVariable: 'password', usernameVariable: 'username')]) {
                    sh 'docker login --username=${username} --password=${password} registry.cn-chengdu.aliyuncs.com'
                    sh 'docker tag art-market-author:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-author:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-author:1.0'
                    echo '作者模块上传成功！'

                    sh 'docker tag art-market-chain:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-chain:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-chain:1.0'
                    echo '区块链模块上传成功！'

                    sh 'docker tag art-market-gateway:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-gateway:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-gateway:1.0'
                    echo '网关模块上传成功！'

                    sh 'docker tag art-market-notice:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-notice:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-notice:1.0'
                    echo '公告模块上传成功！'

                    sh 'docker tag art-market-order:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-order:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-order:1.0'
                    echo '订单模块上传成功！'

                    sh 'docker tag art-market-seckill:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-seckill:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-seckill:1.0'
                    echo '秒杀模块上传成功！'

                    sh 'docker tag art-market-sms:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-sms:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-sms:1.0'
                    echo '短信模块上传成功！'

                    sh 'docker tag art-market-trade:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-trade:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-trade:1.0'
                    echo '交易模块上传成功！'

                    sh 'docker tag art-market-user:1.0 registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-user:1.0'
                    sh 'docker push registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-user:1.0'
                    echo '用户模块上传成功！'
                    echo '全部模块上传完成！'
                }
            }
        }*/
        /**
        stage('Deploy'){
            steps{
                echo '开始部署！'
                withCredentials([usernamePassword(credentialsId: '26abb2c2-ff39-4521-98a1-454f908608d3', passwordVariable: 'password', usernameVariable: 'username')]) {
                    sh 'docker login --username=${username} --password=${password} registry.cn-chengdu.aliyuncs.com'
                    sh 'docker stop art-market-author'
                    sh 'docker stop art-market-chain'
                    sh 'docker stop art-market-gateway'
                    sh 'docker stop art-market-notice'
                    sh 'docker stop art-market-order'
                    sh 'docker stop art-market-seckill'
                    sh 'docker stop art-market-sms'
                    sh 'docker stop art-market-trade'
                    sh 'docker stop art-market-user'

                    sh 'sleep 5'
                    sh 'docker rm art-market-author'
                    sh 'docker rm art-market-chain'
                    sh 'docker rm art-market-gateway'
                    sh 'docker rm art-market-notice'
                    sh 'docker rm art-market-order'
                    sh 'docker rm art-market-seckill'
                    sh 'docker rm art-market-sms'
                    sh 'docker rm art-market-trade'
                    sh 'docker rm art-market-user'
                    sh 'sleep 5'

                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-author:1.0'
                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-chain:1.0'
                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-gateway:1.0'
                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-notice:1.0'
                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-order:1.0'
                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-seckill:1.0'
                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-sms:1.0'
                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-trade:1.0'
                    sh 'docker rmi registry.cn-chengdu.aliyuncs.com/cicd-demo-docker-repositry/art-market-user:1.0'
                    sh 'sleep 5'

                    sh 'docker run -d -p 18881:8881 --name  art-market-author art-market-author:1.0'
                    sh 'docker run -d -p 18888:8888 --name  art-market-chain art-market-chain:1.0'
                    sh 'docker run -d -p 20088:88 --name  art-market-gateway art-market-gateway:1.0'
                    sh 'docker run -d -p 18890:8890 --name  art-market-notice art-market-notice:1.0'
                    sh 'docker run -d -p 19010:9010 --name  art-market-order art-market-order:1.0'
                    sh 'docker run -d -p 19020:9020 --name  art-market-seckill art-market-seckill:1.0'
                    sh 'docker run -d -p 18891:8891 --name  art-market-sms art-market-sms:1.0'
                    sh 'docker run -d -p 19000:9000 --name  art-market-trade art-market-trade:1.0'
                    sh 'docker run -d -p 18889:8889 --name  art-market-user art-market-user:1.0'
                }
                echo '部署完成！'
            }
        }*/

    }
}
