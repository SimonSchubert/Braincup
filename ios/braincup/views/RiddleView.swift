//
//  RiddleView.swift
//  braincup
//
//  Created by Simon Schubert on 26.12.19.
//  Copyright © 2019 Simon Schubert. All rights reserved.
//

import SwiftUI
import shared

struct RiddleView: View {
    var game: RiddleGame
    var answer: (String) -> Void
    var back: () -> Void
    
    // Todo: Find a cleaner solution. Current is based on https://stackoverflow.com/a/58117295
    @ObservedObject var data = ObservableRiddleAnswer()
    class ObservableRiddleAnswer: ObservableObject {
        var game: RiddleGame! = nil
        var answer: (String) -> Void = {_ in }
        var value: String = "" {
            willSet(newValue) {
                if(self.game.isCorrect(input: newValue)) {
                    answer(newValue)
                }
            }
        }
    }
    
    init(game: RiddleGame,
         answer: @escaping (String) -> Void,
         back: @escaping () -> Void) {
        self.game = game
        self.answer = answer
        self.back = back
        data.game = game
        data.answer = answer
    }
    
    var body: some View {
        NavigationView {
            VStack {
                Text(game.quest).font(.headline).padding(.all, 24)
                TextField("", text: $data.value).textFieldStyle(RoundedBorderTextFieldStyle()).frame(minWidth: 0, maxWidth: 160)
                Button(action: {self.answer("")}) {
                    Text("Give up")
                }.buttonStyle(BackgroundButtonStyle()).padding(.top, 16)
            }
            .navigationBarItems(leading: Button(action: { self.back()}){Image("back").foregroundColor(Color(hex: 0xFFED7354))})
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

struct RiddleView_Previews: PreviewProvider {
    static var previews: some View {
        RiddleView(game: RiddleGame(), answer: {_ in }, back: {})
    }
}
