#include "mySocket.h"


void Receiver(int num,SOCKET socket,ClientManager *CM,condition_variable *cv)
{
	// 5-1. 데이터 읽기
	char messageBuffer[MAX_BUFFER] = { NULL };
	wchar_t unimessage[MAX_BUFFER] = { NULL };
	int receiveBytes;
	while (receiveBytes = recv(socket, messageBuffer, MAX_BUFFER, 0))
	{
		if (receiveBytes > 0)
		{
			messageBuffer[receiveBytes] = NULL;
			int nLen = MultiByteToWideChar(CP_UTF8, 0, messageBuffer, strlen(messageBuffer), NULL, NULL);
			MultiByteToWideChar(CP_UTF8, 0, messageBuffer, strlen(messageBuffer), unimessage, nLen);
			unimessage[nLen] = NULL;
			CM->putMessage(Message(num, string(messageBuffer)));

			cout << CM->S.name[num];
			wprintf(L" : %s (%d bytes)\n", unimessage, receiveBytes);
			setFlag(true);
			cv->notify_one();
		}
		else
		{
			break;
		}
	}
	CM->S.putCode(num, 0);
	printf("%d disconnect\n", num);
	closesocket(socket);
}
string toAddress(unsigned int adr)
{
	string str;
	char address[4] = { NULL };
	for (int i = 0; i < 4; i++)
	{
		if (i != 0)
			str.append(".");
		sprintf(address, "%d", adr >> 24);
		adr = adr * 256;
		str.append(address);
	}
	return str;
}
Message ClientManager::getMessage()
{
	Message msg = MSG.pop();
	return msg;
}
void ClientManager::putMessage(Message message)
{
	m.lock();
	MSG.push(message);
	m.unlock();
}

void run(ClientManager *CM,condition_variable *cv)
{
	srand(time(NULL));
	setlocale(LC_ALL, "");
	thread* tmpThread;
	// Winsock Start - windock.dll 로드
	WSADATA WSAData;
	if (WSAStartup(MAKEWORD(2, 2), &WSAData) != 0)
	{
		printf("Error - Can not load 'winsock.dll' file\n");
		return;
	}

	//소켓생성    
	SOCKET listenSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (listenSocket == INVALID_SOCKET)
	{
		printf("Error - Invalid socket\n");
		return;
	}

	// 서버정보 객체설정
	SOCKADDR_IN serverAddr;
	memset(&serverAddr, 0, sizeof(SOCKADDR_IN));
	serverAddr.sin_family = PF_INET;
	serverAddr.sin_port = htons(SERVER_PORT);
	serverAddr.sin_addr.S_un.S_addr = htonl(INADDR_ANY);

	// 2. 소켓설정
	if (bind(listenSocket, (struct sockaddr*) & serverAddr, sizeof(SOCKADDR_IN)) == SOCKET_ERROR)
	{
		printf("Error - Fail bind\n");
		// 6. 소켓종료
		closesocket(listenSocket);
		// Winsock End
		WSACleanup();
		return;
	}

	// 3. 수신대기열생성
	if (listen(listenSocket, 5) == SOCKET_ERROR)
	{
		printf("Error - Fail listen\n");
		// 6. 소켓종료
		closesocket(listenSocket);
		// Winsock End
		WSACleanup();
		return;
	}


	// 연결대기 정보변수 선언
	SOCKADDR_IN clientAddr;
	int addrLen = sizeof(SOCKADDR_IN);
	memset(&clientAddr, 0, addrLen);
	unsigned int clientAdr;
	SOCKET clientSocket;

	cout << "waiting for client..." << endl;
	while (1)
	{
		// 4. 연결대기
		clientSocket = accept(listenSocket, (struct sockaddr*) & clientAddr, &addrLen);
		if(clientSocket == -1)
			continue;
		clientAdr = clientAddr.sin_addr.S_un.S_addr;
		if (clientAdr == inet_addr("127.0.0.1"))
			break;
		int cnum = CM->S.newClient();
		if (cnum != -1)
		{
			tmpThread = new thread(Receiver, cnum, clientSocket, CM, cv);
			CM->S.putCode(cnum, rand());
			CM->S.change(cnum, clientSocket);
			cout << toAddress(clientAdr) << " connected." << endl;
			cout << "client number : " << cnum << " and Code : " << CM->S.code[cnum] << endl;
		}
		else
		{
			cout << "Too many clients!" << endl;
			closesocket(clientSocket);
		}
	}
	// 6-2. 리슨 소켓종료
	closesocket(listenSocket);

	// Winsock End
	WSACleanup();
}