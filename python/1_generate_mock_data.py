import pandas as pd
import random

random.seed(42)
users = [f"user_{i}" for i in range(1, 101)]
categories = ["电子产品", "服装", "图书", "家居", "食品"]
products = []

for i in range(1, 501):
    cat = random.choice(categories)
    products.append({
        "product_id": f"prod_{i}",
        "title": f"{cat}爆款商品{i}",
        "category": cat,
        "price": round(random.uniform(10, 1000), 2)
    })
df_products = pd.DataFrame(products)

interactions = []
for _ in range(5000):
    user = random.choice(users)
    prod = random.choice(products)
    event = random.choices(['view', 'cart', 'purchase'], weights=[0.7, 0.2, 0.1])[0]
    interactions.append({
        "user_id": user,
        "product_id": prod["product_id"],
        "event_type": event,
        "timestamp": pd.Timestamp.now() - pd.Timedelta(days=random.randint(0, 30))
    })
df_interactions = pd.DataFrame(interactions)

df_products.to_csv("../data/products.csv", index=False)
df_interactions.to_csv("../data/interactions.csv", index=False)
print("数据生成完毕！")
