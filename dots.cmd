@echo off
del *.png
FOR /R %%V IN (*.dot) DO (
	dot -O -Tpng %%V
)