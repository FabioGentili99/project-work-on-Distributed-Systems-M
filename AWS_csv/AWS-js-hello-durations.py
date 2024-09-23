import pandas as pd

df = pd.read_csv("sorted_AWS-js-hello.csv")
list = df["message"].tolist()
list2 = []
for x in list:
    list2.append(x.split(" "))

list3 = []
for x in list2:
    list3.append(x[-1])

print(list3)