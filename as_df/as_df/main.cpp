#include "mySocket.h"

bool flag,mxflag;
void setFlag(bool f)
{
	mxflag = f;
}
ClientManager CM;
void connector(condition_variable *cv, mutex *mx)
{
	Sender s;
	Message m;
	unique_lock<mutex> ul(*mx);
	while (!flag)
	{
		cv->wait(ul, []()-> bool {return mxflag && flag; });
		m = CM.getMessage();
		if (m.client != -1)
		{
			if (m.str.length() > 5 && (m.str.substr(0, 5).compare("!name") == 0))
			{
				CM.S.name[m.client] = m.str.substr(5,m.str.length()-5);
			}
			for (int i = 0; i < 10; i++)
			{
				if (CM.S.code[i] != 0)
				{
					CM.S.put(i, m.str);
				}
			}
		}
		m.~Message();
		setFlag(false);
	}
}
int _tmain(int argc, _TCHAR* argv[])
{
	condition_variable cv;
	thread t1,t2;
	char a;
	while (1)
	{
		cout << "서버를 시작하시겠습니까?(Y/N)" << endl;
		cin >> a;
		if (a == 'Y' || a=='y')
		{
			system("CLS");
			flag = false;
			t1 = thread(run, &CM, &cv);
			t2 = thread(connector, &cv, &CM.m);
		}
		else if (a == 'N' || a=='n')
		{
			cout << "서버를 종료합니다" << endl;
			SOCKET local = socket(PF_INET, SOCK_STREAM, 0);
			SOCKADDR_IN serverAddr;
			memset(&serverAddr, 0, sizeof(SOCKADDR_IN));
			serverAddr.sin_family = PF_INET;
			serverAddr.sin_port = htons(SERVER_PORT);
			serverAddr.sin_addr.S_un.S_addr = inet_addr("127.0.0.1");
			connect(local, (struct sockaddr*) & serverAddr, sizeof(serverAddr));
			closesocket(local);
			flag = true;
			t1.join();
			t2.join();
			Sleep(1000);
			break;
		}
		else
		{
			system("CLS");
		}
	}
	return 0;
}