import pandas as pd
from xgboost import XGBClassifier
import os

print("=" * 60)
print("开始生成 XGBoost 原生模型...")
print("=" * 60)

df = pd.read_csv("C:/Users/26659/Desktop/ecommerce-ranking-system/data/feature_matrix.csv")
feature_cols = ['price', 'ctr', 'purchase_rate', 'user_click_7d', 'bm25_score']
X = df[feature_cols].astype('float64')
y = df['label']

print(f"✅ 数据加载完成: {X.shape}")

model = XGBClassifier(
    max_depth=3,
    learning_rate=0.1,
    n_estimators=50,
    eval_metric='logloss',
    use_label_encoder=False,
    random_state=42
)

model.fit(X, y)
print("✅ 模型训练完成")

output_path = "C:/Users/26659/Desktop/ecommerce-ranking-system/java/src/main/resources/xgboost_model.json"
model.save_model(output_path)
print(f"✅ 模型已保存: {output_path}")

file_size = os.path.getsize(output_path)
print(f"   文件大小: {file_size / 1024:.2f} KB")
print("\n🎉 完成！请重启 Java 应用")
