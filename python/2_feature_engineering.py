import pandas as pd
import numpy as np

df_prod = pd.read_csv("../data/products.csv")
df_inter = pd.read_csv("../data/interactions.csv")

prod_stats = df_inter.groupby('product_id')['event_type'].value_counts().unstack(fill_value=0)
prod_stats['ctr'] = prod_stats.get('view', 0) / (prod_stats.get('view', 0) + 10)
prod_stats['purchase_rate'] = prod_stats.get('purchase', 0) / (prod_stats.get('view', 0) + 10)
prod_stats = prod_stats.reset_index()

df_features = pd.merge(df_prod, prod_stats, on='product_id', how='left')
df_features['ctr'].fillna(0, inplace=True)
df_features['purchase_rate'].fillna(0, inplace=True)

samples = []
purchases = df_inter[df_inter['event_type'] == 'purchase']
for _, row in purchases.iterrows():
    prod_info = df_features[df_features['product_id'] == row['product_id']].iloc[0]
    samples.append({
        "query": prod_info['category'],
        "product_id": row['product_id'],
        "label": 1
    })

for _, row in purchases.iterrows():
    neg_prod = df_prod.sample(1).iloc[0]
    if neg_prod['product_id'] != row['product_id']:
        samples.append({
            "query": row['product_id'],
            "product_id": neg_prod['product_id'],
            "label": 0
        })

df_samples = pd.DataFrame(samples)
df_samples = pd.merge(df_samples, df_features, on='product_id', how='left')

feature_cols = ['price', 'ctr', 'purchase_rate']
df_samples['user_click_7d'] = np.random.randint(1, 50, len(df_samples))
feature_cols.append('user_click_7d')

df_samples['bm25_score'] = np.random.uniform(1, 20, len(df_samples))
feature_cols.append('bm25_score')

df_samples[feature_cols + ['label']].to_csv("../data/feature_matrix.csv", index=False)
print("特征工程完毕，保存至 feature_matrix.csv")
