import pandas as pd
import redis
import json
from elasticsearch import Elasticsearch, helpers

df_prod = pd.read_csv("C:/Users/26659/Desktop/ecommerce-ranking-system/data/products.csv")
df_inter = pd.read_csv("C:/Users/26659/Desktop/ecommerce-ranking-system/data/interactions.csv")

r = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)

prod_stats = df_inter.groupby('product_id')['event_type'].value_counts().unstack(fill_value=0)
prod_stats['ctr'] = prod_stats.get('view', 0) / (prod_stats.get('view', 0) + 10)

for _, row in df_prod.iterrows():
    pid = row['product_id']
    stats = prod_stats.loc[pid] if pid in prod_stats.index else {'ctr': 0.0, 'purchase_rate': 0.0}
    feature_json = json.dumps({
        "price": float(row['price']),
        "ctr": float(stats.get('ctr', 0)),
        "purchase_rate": float(stats.get('purchase_rate', 0))
    })
    r.set(f"product:{pid}", feature_json)

import numpy as np
for i in range(1, 101):
    user_json = json.dumps({"user_click_7d": int(np.random.randint(1, 50))})
    r.set(f"user:user_{i}", user_json)

print("Redis 数据加载完毕！")

es = Elasticsearch(["http://localhost:9200"])
index_name = "products"

if es.indices.exists(index=index_name):
    es.indices.delete(index=index_name)

mappings = {
    "properties": {
        "product_id": {"type": "keyword"},
        "title": {"type": "text", "analyzer": "standard"},
        "category": {"type": "keyword"},
        "price": {"type": "float"}
    }
}
es.indices.create(index=index_name, body={"mappings": mappings})

actions = [
    {
        "_index": index_name,
        "_id": row['product_id'],
        "_source": {
            "title": row['title'],
            "category": row['category'],
            "price": row['price']
        }
    }
    for _, row in df_prod.iterrows()
]
helpers.bulk(es, actions)
print("ES 数据加载完毕！")
