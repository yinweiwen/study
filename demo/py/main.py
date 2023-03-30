# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


def print_hi(name):
    # Use a breakpoint in the code line below to debug your script.
    print(f'Hi, {name}')  # Press Ctrl+F8 to toggle the breakpoint.
    print(r'''hello,\n
    world''')
    print(10 // 3)
    print('包含中文的str')
    print('殷伟文'.encode('utf-8'))

    print("年龄{0:.1f} 性别{1}".format(18.11, '男'))
    print("年龄%d 性别%s" % (18, '男'))
    print(f"年龄{25} 性别 {'男'}")

    tuple = ('a', 'b', ['A', 'B'])
    tuple[2][1] = 'd'
    print(tuple)

    for s in tuple:
        print(s)


def pmi():
    heightStr = input("height(m):")
    h = float(heightStr)
    weightStr = input("weight(kg):")
    w = float(weightStr)
    bmi = w / (h * h)
    if bmi < 18.5:
        print("过轻")
    elif bmi < 25:
        print("正常")
    elif bmi < 28:
        print("过重")
    elif bmi < 32:
        print("肥胖")
    else:
        print('严重肥胖')


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print_hi('PyCharm')
    pmi()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
