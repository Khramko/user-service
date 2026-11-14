# Namespaces
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/logging/namespace.yaml

# EFK
kubectl apply -f k8s/logging/elasticsearch.yaml
kubectl apply -f k8s/logging/kibana.yaml
kubectl apply -f k8s/logging/fluent-bit-configmap.yaml
kubectl apply -f k8s/logging/fluent-bit-daemonset.yaml

# Приложение (namespace user-service)
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/job-migration.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/ingress.yaml

# Доступ к Kibana
kubectl port-forward -n logging svc/kibana 5601:5601