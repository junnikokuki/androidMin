while True:
	enemy = coder.find_nearest_enemy()

	# 如果发现敌人，先判断敌人的等级
	if enemy:
		level = coder.get_enemy_level(enemy)

		# 如果敌人等级小于等于3级，去战胜它
		if level <= 3:
			pass

		# 如果敌人等级大于3级，去木箱堆后面躲起来
		if level > 3:
			pass

	else:
		# 没有敌人就到回血点恢复
		pass