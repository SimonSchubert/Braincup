//
//  MainMenuView.swift
//  braincup
//
//  Created by Simon Schubert on 05.10.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct MainMenuView: View {
    var title: String
    var description: String
    var games: [GameType]
    var instructions: (GameType) -> Void
    var score: (GameType) -> Void
    var createChallenge: () -> Void

    var storage = UserStorage()
    
    var body: some View {
        ScrollView {
            VStack {
                Spacer()
                Text(title).font(.title).padding(.top, 16)
                Text(description).font(.body).padding(.horizontal, 16).padding(.bottom, 16).multilineTextAlignment(.center)
                ForEach(games, id: \.name) { gameType in
                    HStack {
                        Button(action: {self.instructions(gameType)}) {
                            HStack {
                                Image(gameType.getImageResource())
                                Text(gameType.getName()).frame(minWidth: 0, maxWidth: 160)
                            }
                        }.buttonStyle(BackgroundButtonStyle()).padding(.top, 12)
                        if(self.storage.getHighScore(gameId: gameType.getId()) > 0) {
                            Button(action: {self.score(gameType)}) {
                                HStack {
                                    Image(gameType.getMedalResource(score: self.storage.getHighScore(gameId: gameType.getId())))
                                    Text("\(self.storage.getHighScore(gameId: gameType.getId()))").frame(minWidth: 15)
                                }
                            }.buttonStyle(BackgroundButtonStyle()).padding(.top, 12)
                        }
                    }
                }
                Button(action: {self.createChallenge()}) {
                    HStack {
                        Image("icons8-create")
                        Text("Create challenge").frame(minWidth: 0, maxWidth: 160)
                    }
                }.buttonStyle(GreenButtonStyle()).padding(.top, 12)
                Image("waiting")
                Spacer()
            }.frame(minHeight: UIScreen.main.bounds.height)
        }
    }
}
