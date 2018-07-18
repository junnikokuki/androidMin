while True:
	gem = coder.find_nearest_gem()
	enemy = coder.find_nearest_enemy()
	
	# 如果发现晶石，去收集
	if gem:
		x = gem.pos.x
		y = gem.pos.y


	# 如果发现敌人，战胜它
	if enemy:
		pass
		