# 敌人会不断追击过来，
# 依次在3条路口击败敌人。

while True:
    # 到第1个路口击败敌人
    coder.move_xy(17,24)
    enemy = coder.find_nearest_enemy()
    if enemy:
        coder.attack(enemy)

    # 到第2个路口击败敌人


    # 到第3个路口击败敌人